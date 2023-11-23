package ulisboa.tecnico.agents.observation;

import ulisboa.tecnico.agents.ICharacter;

public class ReceivedChatObservation implements IObservation<IObserver> {

    // Private attributes
    private final ICharacter from;
    private final String chat;

    // Constructors

    public ReceivedChatObservation(ICharacter from, String chat) {
        this.from = from;
        this.chat = chat;
    }

    // Getters and setters

    public ICharacter getFrom() {
        return from;
    }

    public String getChat() {
        return chat;
    }

    // Other methods

    @Override
    public void accept(IObserver observer) {
        observer.receivedChatFrom(this);
    }
}
