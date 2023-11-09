package ulisboa.tecnico.minesocieties.visitors;

import ulisboa.tecnico.agents.ICharacter;
import ulisboa.tecnico.agents.observation.IObservation;
import ulisboa.tecnico.agents.observation.WeatherChangeObservation;
import ulisboa.tecnico.minesocieties.agents.observation.ISocialObserver;

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
    public void observeWeatherChange(WeatherChangeObservation observation) {
        switch (observation.getWeatherType()) {
            case DOWNFALL -> {
                lastExplanation = "Rain started to fall";
            }
            case CLEAR -> {
                lastExplanation = "It's no longer raining";
            }
        }
    }

    @Override
    public void receivedChatFrom(ICharacter from, String chat) {
        lastExplanation = from.getName() + " said {" + chat + "}";
    }

    @Override
    public void receivedAnyObservation(IObservation<?> observation) {
        // This visitor only explains concrete observations
    }
}
