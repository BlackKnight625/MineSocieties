package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.llms.LLMMessage;
import ulisboa.tecnico.llms.LLMRole;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.actions.exceptions.MalformedNewStateResponseException;
import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import static ulisboa.tecnico.minesocieties.utils.PromptUtils.*;

/**
 *  Represents all the information that an Agent can have. Accessing an Agent's state should not be done on the
 * Main thread, as it is certain that non-main threads will access it. As such, thread-safety is required, so by
 * accessing an Agent's State with the Main thread, it's at risk of being blocked.
 */
public class AgentState implements IExplainableContext {

    // Private attributes

    private AgentMemory memory;
    private AgentMoods moods = new AgentMoods();
    private AgentPersonalities personalities = new AgentPersonalities();
    private AgentPersona persona;
    private Lock stateLock = new ReentrantLock();

    // Constructors

    public AgentState() {}

    public AgentState(AgentPersona persona, AgentLocation home) {
        this.memory = new AgentMemory(home);
        this.persona = persona;
    }

    // Getters and setters

    public AgentMemory getMemory() {
        return memory;
    }

    public AgentMoods getMoods() {
        return moods;
    }

    public AgentPersonalities getPersonalities() {
        return personalities;
    }

    public AgentPersona getPersona() {
        return persona;
    }

    // Other methods

    public void startStateModification() {
        stateLock.lock();
    }

    public void finishStateModification() {
        stateLock.unlock();
    }

    /**
     *  Gives this agent an initial description. This should only be called when the AgentState is brand new
     * and the agent does not have memories yet. Otherwise, this can mess up the existing memories.
     *  This prompts the LLM into helping to organize the agent's description into opinios, reflections, etc. As such,
     * this is a blocking operation.
     * @param firstMemories
     *  The first memories, in Natural Language, of this agent. This should not include any type of parenthesis
     * '[]', '{}', or the symbol '|', as they are used to instruct the LLM to tidy up the memories.
     * @throws MalformedNewStateResponseException
     *  Thrown if the LLM's response does not conform to the given instructions.
     */
    public void insertDescriptionSync(String firstMemories) throws MalformedNewStateResponseException {
        requestStateChangeSync(getPromptForDescriptionInterpretationSync(firstMemories));
    }

    /**
     *  Gives this agent an initial description. This should only be called when the AgentState is brand new
     * and the agent does not have memories yet. Otherwise, this can mess up the existing memories.
     *  This prompts the LLM into helping to organize the agent's description into opinios, reflections, etc. That's done
     * asynchronously. This method returns immediately.
     * @param firstMemories
     *  The first memories, in Natural Language, of this agent. This should not include any type of parenthesis
     * '[]', '{}', or the symbol '|', as they are used to instruct the LLM to tidy up the memories.
     */
    public void insertDescriptionAsync(String firstMemories) {
        requestStateChangeAsync(() -> getPromptForDescriptionInterpretationSync(firstMemories));
    }

    /**
     *  Called when the state should suffer changes decided by the LLM. Its response will then be interpreted and
     * changes will be applied to this state accordingly. {@link #getStateFormat()} should be included in the messages,
     * as it sets the format of the responses that will be interpreted.
     * @param messages
     *  The prompts to be sent to the LLM
     */
    public void requestStateChangeSync(List<LLMMessage> messages) {
        startStateModification();

        try {
            interpretNewStateResponse(MineSocieties.getPlugin().getLLMManager().promptSync(messages));
        } finally {
            finishStateModification();
        }
    }

    /**
     *  Called when the state should suffer changes decided by the LLM. Its response will then be interpreted and
     * changes will be applied to this state accordingly. {@link #getStateFormat()} should be included in the messages,
     * as it sets the format of the responses that will be interpreted.
     * @param messagesSuplier
     *  A supplier for the prompts
     */
    public void requestStateChangeAsync(Supplier<List<LLMMessage>> messagesSuplier) {
        MineSocieties.getPlugin().getLLMManager().promptAsyncSupplyMessageAsync(
                () -> {
                    startStateModification(); // Once the prompt is requested, this state becomes locked

                    return messagesSuplier.get();
                },
                response -> {
                    interpretNewStateResponse(response);
                    finishStateModification(); // After changes are applied to the state, the state becomes unlocked
                },
                throwable -> finishStateModification() // If there's a problem during prompting, the state becomes unlocked
        );
    }

    private List<LLMMessage> getPromptForDescriptionInterpretationSync(String description) {
        String name = persona.getName();
        List<LLMMessage> messageList = new ArrayList<>(4);

        // Telling the model exactly what to do
        messageList.add(new LLMMessage(LLMRole.SYSTEM,
        "You are a people description analyzer. You will receive people's descriptions " +
                "inside brackets '{}' and must interpret it. " +
                getStateFormat())
        );

        // Giving an example of input to the model
        messageList.add(new LLMMessage(LLMRole.USER,
                "Rafael: {Rafael is a software engineer. He loves chocolate. He thinks Francisco is tall, and is weird for not " +
                     "liking pineapple on pizza. Rafael is smart and knows funny dark humour jokes. He's happy with himself. " +
                        "He's going to a party next week. He needs to cook dinner tonight.}"
                )
        );

        // Giving an example of output to the model
        messageList.add(new LLMMessage(LLMRole.ASSISTANT,
                PERSONALITIES_FORMAT_BEGIN + "{engineer|chocolate-lover|smart|good-sense-of-humour}\n" +
                        EMOTIONS_FORMAT_BEGIN + "{happy}\n" +
                        REFLECTIONS_FORMAT_BEGIN + "{Rafael likes software engineering because that's what he does and he's happy with " +
                        "himself|Rafael thinks that not liking pineapple on pizza is weird|Rafael knows funny dark humour jokes|" +
                        "Rafael knows Francisco does not like pineapple on pizza}\n" +
                        OPINIONS_FORMAT_BEGIN + "{Francisco[Rafael thinks Francisco is tall and weird]}\n" +
                        SHORT_MEMORY_FORMAT_BEGIN + "{Rafael needs to cook dinner tonight}\n" +
                        LONG_MEMORY_FORMAT_BEGIN + "{Rafael has a party to attend next week}\n" +
                        "Explanation: Since Rafael is a software engineer and he's happy with himself, this means he likes software engineering. " +
                        "Rafael is not tall, however, Francisco is tall according to Rafael. Cooking dinner tonight is something that " +
                        "Rafael needs to remember only for this day, as such, it's considered short-term memory. Rafael is going to a party " +
                        "next week, which is something that he must remember for a longer time, hence, it's considered long-term memory."
                )
        );

        // Giving it the desired input
        messageList.add(new LLMMessage(LLMRole.USER,
                name + ": {" + description + "}"
                )
        );

        return messageList;
    }

    public String getStateFormat() {
        return "Write down the personality traits that best describe them as " + PERSONALITIES_FORMAT +
                " (traits should be a single word or hyphenated words, and they must belong to them), " +
                "and their feelings as " + EMOTIONS_FORMAT + " (single word). " +
                "Infer knowledge and write it as " + REFLECTIONS_FORMAT +
                " (full sentence with the inferred knowledge). " +
                "Share their opinions about others as " + OPINIONS_FORMAT +
                " Record short-term memories as " + SHORT_MEMORY_FORMAT + ". " +
                " Record long-term memories and important details as " + LONG_MEMORY_FORMAT +
                ". If a list should be empty, write '{}'. Finally, write a short explanation for your choices.\n";
    }

    /**
     *  Called when a new AgentState should be created from an LLM's response to something. This will
     * replace some of the contents inside this AgentState
     * @param response
     *  The response containing the new information to replace this AgentState
     * @throws MalformedNewStateResponseException
     *  Thrown when the response is not formatted correctly
     */
    public void interpretNewStateResponse(String response) throws MalformedNewStateResponseException {
        int personalitiesIndex = response.indexOf(PERSONALITIES_FORMAT_BEGIN);
        int emotionsIndex = response.indexOf(EMOTIONS_FORMAT_BEGIN);
        int reflectionsIndex = response.indexOf(REFLECTIONS_FORMAT_BEGIN);
        int opinionsIndex = response.indexOf(OPINIONS_FORMAT_BEGIN);
        int shortMemoryIndex = response.indexOf(SHORT_MEMORY_FORMAT_BEGIN);
        int longMemoryIndex = response.indexOf(LONG_MEMORY_FORMAT_BEGIN);

        // Checking if the LLM replied with the lists names
        if (personalitiesIndex == -1) {
            throw new MalformedNewStateResponseException("LLM did not create a Personalities list.", response);
        }

        if (emotionsIndex == -1) {
            throw new MalformedNewStateResponseException("LLM did not create an Emotions list.", response);
        }

        if (reflectionsIndex == -1) {
            throw new MalformedNewStateResponseException("LLM did not create a Reflections list.", response);
        }

        if (opinionsIndex == -1) {
            throw new MalformedNewStateResponseException("LLM did not create an Opinions list.", response);
        }

        if (shortMemoryIndex == -1) {
            throw new MalformedNewStateResponseException("LLM did not create a ShortMemory list.", response);
        }

        if (longMemoryIndex == -1) {
            throw new MalformedNewStateResponseException("LLM did not create a LongMemory list.", response);
        }

        // Interpreting the personalities
        try {
            personalities.reset();

            String[] personalities = response.substring(
                    response.indexOf('{', personalitiesIndex) + 1,
                    response.indexOf('}', personalitiesIndex)
            ).split("\\|");

            for (String personality: personalities) {
                if (!personality.isEmpty()) {
                    this.personalities.addState(personality);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new MalformedNewStateResponseException("LLM malformed the Personalities list.", response);
        }

        // Interpreting the emotions
        try {
            moods.reset();

            String[] emotions = response.substring(
                    response.indexOf('{', emotionsIndex) + 1,
                    response.indexOf('}', emotionsIndex)
            ).split("\\|");

            for (String emotion: emotions) {
                if (!emotion.isEmpty()) {
                    this.moods.addState(emotion);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new MalformedNewStateResponseException("LLM malformed the Emotions list.", response);
        }

        // Interpreting the reflections
        try {
            String[] reflections = response.substring(
                    response.indexOf('{', reflectionsIndex) + 1,
                    response.indexOf('}', reflectionsIndex)
            ).split("\\|");

            AgentReflections agentReflections = memory.getReflections();

            agentReflections.reset();

            for (String reflection : reflections) {
                if (!reflection.isEmpty()) {
                    agentReflections.addMemorySection(new Reflection(Instant.now(), reflection));
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new MalformedNewStateResponseException("LLM malformed the Reflections list.", response);
        }

        // Interpreting the opinions
        try {
            String[] opinions = response.substring(
                    response.indexOf('{', opinionsIndex) + 1,
                    response.indexOf('}', opinionsIndex)
            ).split("\\|");

            AgentOpinions agentOpinions = memory.getOpinions();

            agentOpinions.reset();

            for (String nameAndOpinion : opinions) {
                if (!nameAndOpinion.isEmpty()) {
                    String[] nameAndOpinionSplit = nameAndOpinion.split("\\[");
                    String name = nameAndOpinionSplit[0];
                    String opinion = nameAndOpinionSplit[1].substring(0, nameAndOpinionSplit[1].length() - 1); // Deleting the last ']' char

                    agentOpinions.formOpinion(name, new Opinion(opinion));
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new MalformedNewStateResponseException("LLM malformed the Opinions list.", response);
        }

        // Interpreting the short term memory
        try {
            String[] shortMemories = response.substring(
                    response.indexOf('{', shortMemoryIndex) + 1,
                    response.indexOf('}', shortMemoryIndex)
            ).split("\\|");

            AgentShortTermMemory agentShortTermMemory = memory.getShortTermMemory();

            agentShortTermMemory.reset();

            for (String shortMemory : shortMemories) {
                if (!shortMemory.isEmpty()) {
                    agentShortTermMemory.addMemorySection(new ShortTermMemorySection(Instant.now(), shortMemory));
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new MalformedNewStateResponseException("LLM malformed the Short-Term Memory list.", response);
        }

        // Interpreting the long term memory
        try {
            String[] longMemories = response.substring(
                    response.indexOf('{', longMemoryIndex) + 1,
                    response.indexOf('}', longMemoryIndex)
            ).split("\\|");

            AgentLongTermMemory agentLongTermMemory = memory.getLongTermMemory();

            agentLongTermMemory.reset();

            for (String longMemory : longMemories) {
                if (!longMemory.isEmpty()) {
                    agentLongTermMemory.addMemorySection(new LongTermMemorySection(Instant.now(), longMemory));
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new MalformedNewStateResponseException("LLM malformed the Long-Term Memory list.", response);
        }
    }

    // Saves this state in a file
    public void saveSync() {
        // TODO
    }

    public void saveAsync() {
        // TODO
    }

    @Override
    public String accept(IContextVisitor visitor) {
        return visitor.explainState(this);
    }
}
