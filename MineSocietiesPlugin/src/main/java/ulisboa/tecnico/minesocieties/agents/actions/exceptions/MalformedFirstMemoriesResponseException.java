package ulisboa.tecnico.minesocieties.agents.actions.exceptions;

public class MalformedFirstMemoriesResponseException extends RuntimeException {

    // Private attributes

    private String firstMemories;
    private String llmResponse;

    // Constructors

    public MalformedFirstMemoriesResponseException(String whatWentWrong, String firstMemories, String llmResponse) {
        super(whatWentWrong + ". Agent's first memories: {" + firstMemories + "}. LLM's response: " + llmResponse);

        this.firstMemories = firstMemories;
        this.llmResponse = llmResponse;
    }

    // Getters and setters

    public String getFirstMemories() {
        return firstMemories;
    }

    public String getLlmResponse() {
        return llmResponse;
    }
}
