package ulisboa.tecnico.agents.observation;

import ulisboa.tecnico.agents.ICharacter;

/**
 *  Class used to represent something that a Character may observe.
 *  C is a type argument to allow observations to call methods of specific agent types
 */
public interface IObservation<C extends ICharacter> {

    /**
     *  Method abiding to the Visitor Design Pattern. Implementors of the IObservation interface
     * should override this and call the respective ICharacter's method that should deal with
     * the concrete observation
     * @param character
     *  The character that is observing this instance
     */
    void accept(C character);
}
