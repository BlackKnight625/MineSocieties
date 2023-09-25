package ulisboa.tecnico.agents.actions;

import ulisboa.tecnico.agents.ICharacter;

/**
 *  Class that represents character actions that need some time to get executed
 * @param <T>
 *  The type of the character this action applies to
 */
public abstract class TemporalAction<T extends ICharacter> implements IAction<T> {

    // Private attributes
    private int elapsedTicks = 0;

    // Other methods

    public abstract void start(T actioner);

    public abstract ActionStatus tick(T actioner, int elapsedTicks);

    @Override
    public ActionStatus act(T actioner) {
        if (elapsedTicks == 0) {
            start(actioner);
        }

        ActionStatus status = tick(actioner, elapsedTicks);

        elapsedTicks++;

        return status;
    }
}
