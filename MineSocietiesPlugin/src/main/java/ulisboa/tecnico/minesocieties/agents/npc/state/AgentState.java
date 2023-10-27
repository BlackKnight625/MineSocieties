package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.llms.LLMMessage;
import ulisboa.tecnico.llms.LLMRole;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.actions.exceptions.MalformedFirstMemoriesResponseException;
import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AgentState implements IExplainableContext {

    // Private attributes

    private AgentMemory memory;
    private AgentMoods moods = new AgentMoods();
    private AgentPersonalities personalities = new AgentPersonalities();
    private AgentPersona persona;

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

    /**
     *  Gives this agent an initial description. This should only be called when the AgentState is brand new
     * and the agent does not have memories yet. Otherwise, this can mess up the existing memories.
     *  This prompts the LLM into helping to organize the agent's description into opinios, reflections, etc. As such,
     * this is a blocking operation.
     * @param firstMemories
     *  The first memories, in Natural Language, of this agent. This should not include any type of parenthesis
     * '[]', '{}', or the symbol '|', as they are used to instruct the LLM to tidy up the memories.
     * @throws MalformedFirstMemoriesResponseException
     *  Thrown if the LLM's response does not conform to the given instructions.
     */
    public void insertDescriptionSync(String firstMemories) throws MalformedFirstMemoriesResponseException {
        var messages = getPromptForDescriptionInterpretation(firstMemories);
        String response = MineSocieties.getPlugin().getLLMManager().promptSync(messages);

        interpretFirstMemoriesResponse(firstMemories, response);
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
        var messages = getPromptForDescriptionInterpretation(firstMemories);

        MineSocieties.getPlugin().getLLMManager().promptAsync(messages, response -> interpretFirstMemoriesResponse(firstMemories, response));
    }

    private List<LLMMessage> getPromptForDescriptionInterpretation(String description) {
        String name = persona.getName();
        List<LLMMessage> messageList = new ArrayList<>(4);

        // Telling the model exactly what to do
        messageList.add(new LLMMessage(LLMRole.SYSTEM,
        "You are a people description analyzer. You will receive people's descriptions " +
                "inside brackets '{}' and must interpret it. " +
                "Write down the personality traits that best describe them as '|Personalities{<trait1>|<trait2>|...}' " +
                "(traits should be a single word or hyphenated words, and they must belong to them), " +
                "and their feelings as '|Emotions{<emotion1>|<emotion2>|...}' (single word). " +
                "Infer knowledge and write it as '|Reflections{<thought1>|<thought2>|...}' " +
                "(full sentence with the inferred knowledge). " +
                "Share their opinions about others as '|Opinions{<name1>[<opinion1>]|<name2>[<opinion2>]|...}' " +
                "Record short-term memories as '|ShortMemory{<sentence1>|<sentence2>|...}'. " +
                "If a list should be empty, write '{}'. Finally, write a short explanation for your choices. "
                )
        );

        // Giving an example of input to the model
        messageList.add(new LLMMessage(LLMRole.USER,
                "{Rafael is a software engineer. He loves chocolate. He thinks Francisco is tall, and is weird for not " +
                     "liking pineapple on pizza. Rafael is smart and knows funny dark humour jokes. He's happy with himself. " +
                        "He's going to a party next week.}"
                )
        );

        // Giving an example of output to the model
        messageList.add(new LLMMessage(LLMRole.ASSISTANT,
                "|Personalities{engineer|chocolate-lover|smart|good-sense-of-humour}\n" +
                        "|Emotions{happy}\n" +
                        "|Reflections{Rafael likes software engineering because that's what he does and he's happy with " +
                        "himself|Rafael thinks that not liking pineapple on pizza is weird|Rafael knows funny dark humour jokes|" +
                        "Rafael knows Francisco does not like pineapple on pizza}\n" +
                        "|Opinions{Francisco[Rafael thinks Francisco is tall and weird]}\n" +
                        "|ShortMemory{Rafael has a party to attend next week}\n" +
                        "Explanation: Since Rafael is a software engineer and he's happy with himself, this means he likes software engineering. " +
                        "Rafael is not tall, however, Francisco is tall according to Rafael."
                )
        );

        // Giving it the desired input
        messageList.add(new LLMMessage(LLMRole.USER,
                "{" + description + "}"
                )
        );

        return messageList;

        /*
                "Interpret " + name + "'s description: {" + description + "}\n" +
                "Write down the personality traits that best describes " + name + " as '|Personalities{<trait1>|<trait2>|...}' " +
                "(traits should be a single word or hyphenated words, and they must belong to " + name + "). " +
                "and " + name + "'s feelings as '|Emotions{<emotion1>|<emotion2>|...}' (single word). " +
                "Infer knowledge and write it as '|Reflections{<thought1>|<thought2>|...}' " +
                "(full sentence with the inferred knowledge)" +
                "Share " + name + "'s opinions about others as '|Opinions{<name1>[<opinion1>]|<name2>[<opinion2>]|...}' " +
                "Record short-term memories as '|ShortMemory{<sentence1>|<sentence2>|...}'. " +
                "If a list should be empty, write '{}'. Finally, write a short explanation for your choices. " +
                "Example:\nMemories- {Rafael is a software engineer. He loves chocolate. He thinks Francisco is tall, and is weird for not " +
                "liking pineapple on pizza. Rafael is smart and knows funny dark humour jokes. He's happy with himself. He's going to " +
                "a party next week.}\n" +
                "|Personalities{engineer|chocolate-lover|smart|good-sense-of-humour}\n" +
                "|Emotions{happy}\n" +
                "|Reflections{Rafael likes software engineering because that's what he does and he's happy with " +
                "himself|Rafael thinks that not liking pineapple on pizza is weird|Rafael knows funny dark humour jokes|" +
                "Rafael knows Francisco does not like pineapple on pizza}\n" +
                "|Opinions{Francisco[Rafael thinks Francisco is tall and weird]}\n" +
                "|ShortMemory{Rafael has a party to attend next week}\n" +
                "Notice how 'weird' is not part of Rafael's personality, since it is Francisco who is described as weird.";

         */
    }

    private void interpretFirstMemoriesResponse(String firstMemories, String response) throws MalformedFirstMemoriesResponseException {
        int personalitiesIndex = response.indexOf("|Personalities");
        int emotionsIndex = response.indexOf("|Emotions");
        int reflectionsIndex = response.indexOf("|Reflections");
        int opinionsIndex = response.indexOf("|Opinions");
        int shortMemoryIndex = response.indexOf("|ShortMemory");

        Instant now = Instant.now();

        // Checking if the LLM replied with the lists names
        if (personalitiesIndex == -1) {
            throw new MalformedFirstMemoriesResponseException("LLM did not create a Personalities list.", firstMemories, response);
        }

        if (emotionsIndex == -1) {
            throw new MalformedFirstMemoriesResponseException("LLM did not create an Emotions list.", firstMemories, response);
        }

        if (reflectionsIndex == -1) {
            throw new MalformedFirstMemoriesResponseException("LLM did not create a Reflections list.", firstMemories, response);
        }

        if (opinionsIndex == -1) {
            throw new MalformedFirstMemoriesResponseException("LLM did not create an Opinions list.", firstMemories, response);
        }

        if (shortMemoryIndex == -1) {
            throw new MalformedFirstMemoriesResponseException("LLM did not create a ShortMemory list.", firstMemories, response);
        }

        // Interpreting the personalities
        try {
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
            throw new MalformedFirstMemoriesResponseException("LLM malformed the Personalities list.", firstMemories, response);
        }

        // Interpreting the emotions
        try {
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
            throw new MalformedFirstMemoriesResponseException("LLM malformed the Emotions list.", firstMemories, response);
        }

        // Interpreting the reflections
        try {
            String[] reflections = response.substring(
                    response.indexOf('{', reflectionsIndex) + 1,
                    response.indexOf('}', reflectionsIndex)
            ).split("\\|");

            AgentReflections agentReflections = memory.getReflections();

            for (String reflection : reflections) {
                if (!reflection.isEmpty()) {
                    agentReflections.addMemorySection(new Reflection(now, reflection));
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new MalformedFirstMemoriesResponseException("LLM malformed the Reflections list.", firstMemories, response);
        }

        // Interpreting the opinions
        try {
            String[] opinions = response.substring(
                    response.indexOf('{', opinionsIndex) + 1,
                    response.indexOf('}', opinionsIndex)
            ).split("\\|");

            AgentOpinions agentOpinions = memory.getOpinions();

            for (String nameAndOpinion : opinions) {
                if (!nameAndOpinion.isEmpty()) {
                    String[] nameAndOpinionSplit = nameAndOpinion.split("\\[");
                    String name = nameAndOpinionSplit[0];
                    String opinion = nameAndOpinionSplit[1].substring(0, nameAndOpinionSplit[1].length() - 1); // Deleting the last ']' char

                    agentOpinions.formOpinion(name, new Opinion(opinion));
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new MalformedFirstMemoriesResponseException("LLM malformed the Opinions list.", firstMemories, response);
        }

        // Interpreting the reflections
        try {
            String[] shortMemories = response.substring(
                    response.indexOf('{', shortMemoryIndex) + 1,
                    response.indexOf('}', shortMemoryIndex)
            ).split("\\|");

            AgentShortTermMemory agentShortTermMemory = memory.getShortTermMemory();

            for (String shortMemory : shortMemories) {
                if (!shortMemory.isEmpty()) {
                    agentShortTermMemory.addMemorySection(new ShortTermMemorySection(now, shortMemory));
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new MalformedFirstMemoriesResponseException("LLM malformed the Short-Term Memory list.", firstMemories, response);
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
