package ulisboa.tecnico.llms;

public enum LLMRole {
    USER("user"),
    SYSTEM("system"),
    ASSISTANT("assistant"),
    ;

    // Private attributes

    private final String chatGPTRoleName;

    // Constructors

    LLMRole(String chatGPTRoleName) {
        this.chatGPTRoleName = chatGPTRoleName;
    }

    // Getters and setters

    public String getChatGPTRoleName() {
        return chatGPTRoleName;
    }
}
