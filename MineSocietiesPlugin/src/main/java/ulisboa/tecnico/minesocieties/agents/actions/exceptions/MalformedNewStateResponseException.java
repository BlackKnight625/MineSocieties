package ulisboa.tecnico.minesocieties.agents.actions.exceptions;

public class MalformedNewStateResponseException extends RuntimeException {

    // Private attributes
    private final String llmResponse;

    // Constructors

    public MalformedNewStateResponseException(String whatWentWrong, String llmResponse) {
        super(whatWentWrong + ". LLM's response: " + llmResponse);

        this.llmResponse = llmResponse;
    }

    // Getters and setters

    public String getLlmResponse() {
        return llmResponse;
    }
}
