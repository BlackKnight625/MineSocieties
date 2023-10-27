package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.actions.exceptions.MalformedFirstMemoriesResponseException;
import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

import java.time.Instant;

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
        String prompt = getPromptForDescriptionInterpretation(firstMemories);
        String response = MineSocieties.getPlugin().getLLMManager().promptSync(prompt);

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
        String prompt = getPromptForDescriptionInterpretation(firstMemories);

        MineSocieties.getPlugin().getLLMManager().promptAsync(prompt, response -> interpretFirstMemoriesResponse(firstMemories, response));
    }

    private String getPromptForDescriptionInterpretation(String description) {
        String name = persona.getName();

        /*
        Write down Alex Johnson's personalities as '|Personalities{<trait1>|<trait2>|...}'
        (traits should be a single word or hyphenated words)
        and emotions as '|Emotions{<emotion1>|<emotion2>|...}' (single word).
        Guess their conclusions as '|Reflections{<thought1>|<thought2>|...}'
        (full sentence with the inferred knowledge).
        Share Alex Johnson's opinions about others as
        '|Opinions{<personName1>[<opinion1>]|<personName2>[<opinion2>]|...}'
        (ex: |Opinions{Jon[Alex thinks Jon is funny]}).
        Record short-term memories as '|ShortMemory{<sentence1>|<sentence2>|...}'.
        Explain with few words your choices after the last bracket.
         */

        return
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
                "|Reflections{Rafael likes software engineering because that's what he is and he's happy with " +
                "himself|Rafael thinks that not liking pineapple on pizza is weird|Rafael knows funny dark humour jokes|" +
                "Rafael knows Francisco does not like pineapple on pizza}\n" +
                "|Opinions{Francisco[Rafael thinks Francisco is tall and weird]}\n" +
                "|ShortMemory{Rafael has a party to attend next week}\n" +
                "<explanation would go here>";
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
