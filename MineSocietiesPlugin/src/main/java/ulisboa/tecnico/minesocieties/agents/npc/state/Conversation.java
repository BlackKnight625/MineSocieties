package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.agents.observation.ReceivedChatObservation;
import ulisboa.tecnico.minesocieties.agents.SocialCharacter;
import ulisboa.tecnico.minesocieties.agents.observation.wrapped.SocialReceivedChatFromObservation;

import java.time.Instant;

public class Conversation extends InstantMemory {

    // Private attributes

    private final String conversation;
    private final CharacterReference speaker;
    private final CharacterReference listener;

    // Constructors

    public Conversation(Instant instant, String conversation, CharacterReference speaker, CharacterReference listener) {
        super(instant);
        this.conversation = conversation;
        this.speaker = speaker;
        this.listener = listener;
    }

    public Conversation(ReceivedChatObservation observation, SocialCharacter receiver) {
        this(
                Instant.now(),
                observation.getChat(),
                new CharacterReference(observation.getFrom()),
                new CharacterReference(receiver)
        );
    }

    public Conversation(SocialReceivedChatFromObservation observation, SocialCharacter receiver) {
        this(observation.getObservation(), receiver);
    }
    // Getters and setters

    public String getConversation() {
        return conversation;
    }

    public CharacterReference getSpeaker() {
        return speaker;
    }

    public CharacterReference getListener() {
        return listener;
    }
}
