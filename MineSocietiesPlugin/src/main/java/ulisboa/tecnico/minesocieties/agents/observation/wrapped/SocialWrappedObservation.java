package ulisboa.tecnico.minesocieties.agents.observation.wrapped;

import ulisboa.tecnico.agents.observation.IObservation;
import ulisboa.tecnico.minesocieties.agents.observation.ISocialObserver;

public abstract class SocialWrappedObservation<T extends IObservation<?>> implements IObservation<ISocialObserver> {

    // Private attributes

    private final T observation;

    // Constructors

    public SocialWrappedObservation(T observation) {
        this.observation = observation;
    }

    // Getters and setters

    public T getObservation() {
        return observation;
    }
}
