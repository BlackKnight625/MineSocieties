package ulisboa.tecnico.minesocieties.agents.observation.wrapped;

import ulisboa.tecnico.agents.observation.WeatherChangeObservation;
import ulisboa.tecnico.minesocieties.agents.observation.ISocialObserver;

public class SocialWeatherChangeObservation extends SocialWrappedObservation<WeatherChangeObservation> {

    // Constructors

    public SocialWeatherChangeObservation(WeatherChangeObservation observation) {
        super(observation);
    }

    // Other methods

    @Override
    public void accept(ISocialObserver observer) {
        observer.observeWeatherChange(this);
    }
}
