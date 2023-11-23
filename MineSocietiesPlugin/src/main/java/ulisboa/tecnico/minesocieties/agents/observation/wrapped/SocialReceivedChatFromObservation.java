package ulisboa.tecnico.minesocieties.agents.observation.wrapped;

import ulisboa.tecnico.agents.observation.ReceivedChatObservation;
import ulisboa.tecnico.agents.observation.WeatherChangeObservation;
import ulisboa.tecnico.minesocieties.agents.observation.ISocialObserver;

public class SocialReceivedChatFromObservation extends SocialWrappedObservation<ReceivedChatObservation> {

    // Constructors

    public SocialReceivedChatFromObservation(ReceivedChatObservation observation) {
        super(observation);
    }

    // Other methods

    @Override
    public void accept(ISocialObserver observer) {
        observer.receivedChatFrom(this);
    }
}
