package ulisboa.tecnico.minesocieties.agents.npc.state;

import java.time.Instant;

public class Conversation extends InstantMemory {

    // Private attributes

    private final String conversation;
    private final AgentReference speaker;
    private final AgentReference listener;

    // Constructors

    public Conversation(Instant instant, String conversation, AgentReference speaker, AgentReference listener) {
        super(instant);
        this.conversation = conversation;
        this.speaker = speaker;
        this.listener = listener;
    }

    // Getters and setters

    public String getConversation() {
        return conversation;
    }

    public AgentReference getSpeaker() {
        return speaker;
    }

    public AgentReference getListener() {
        return listener;
    }
}
