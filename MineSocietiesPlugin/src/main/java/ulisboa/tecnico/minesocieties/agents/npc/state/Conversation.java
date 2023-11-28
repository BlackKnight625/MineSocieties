package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.minesocieties.agents.SocialCharacter;
import ulisboa.tecnico.minesocieties.agents.observation.wrapped.SocialReceivedChatFromObservation;

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

    public Conversation(SocialReceivedChatFromObservation observation, SocialCharacter receiver) {
        this(
                Instant.now(),
                observation.getObservation().getChat(),
                new AgentReference(observation.getObservation().getFrom()),
                new AgentReference(receiver)
        );
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
