package ulisboa.tecnico.agents.observation;

/**
 *  Class used to represent something that a an IObserver may observe.
 *  O is a type argument to allow observations to call methods of more specific Observer types
 */
public interface IObservation<O extends IObserver> {

    /**
     *  Method abiding to the Visitor Design Pattern. Implementors of the IObservation interface
     * should override this and call the respective IObserver's method that should deal with
     * the concrete observation
     * @param observer
     *  The observer that is observing this instance
     */
    void accept(O observer);
}
