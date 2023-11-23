package ulisboa.tecnico.minesocieties.visitors;

import ulisboa.tecnico.agents.ICharacter;
import ulisboa.tecnico.agents.observation.IObservation;
import ulisboa.tecnico.agents.observation.IObserver;
import ulisboa.tecnico.agents.observation.ReceivedChatObservation;
import ulisboa.tecnico.agents.observation.WeatherChangeObservation;
import ulisboa.tecnico.minesocieties.agents.observation.ISocialObserver;
import ulisboa.tecnico.minesocieties.agents.observation.wrapped.SocialReceivedChatFromObservation;
import ulisboa.tecnico.minesocieties.agents.observation.wrapped.SocialWeatherChangeObservation;

/**
 *  This class allows the explanation, in natural language, of observations that can be made by social agents.
 *
 *  Since the IObserver interface's methods return void, the getLastExplanation() method should be called right after
 *  an explanation is requested from this Explainer.
 */
public class ObservationExplainerVisitor implements ISocialObserver {

    // Private attributes

    private String lastExplanation = "";

    // Getters and setters

    public String getLastExplanation() {
        return lastExplanation;
    }

    // Visitor methods

    @Override
    public void observeWeatherChange(SocialWeatherChangeObservation observation) {
        switch (observation.getObservation().getWeatherType()) {
            case DOWNFALL -> {
                lastExplanation = "Rain started to fall";
            }
            case CLEAR -> {
                lastExplanation = "It's no longer raining";
            }
        }
    }

    @Override
    public void receivedChatFrom(SocialReceivedChatFromObservation observation) {
        lastExplanation = observation.getObservation().getFrom().getName() + " said {" + observation.getObservation().getChat() + "}";
    }
}
