package ulisboa.tecnico.agents.actions;

import ulisboa.tecnico.agents.npc.IAgent;

/**
 *  Class that represents character actions that need some time to get executed
 * @param <T>
 *  The type of the character this action applies to
 */
public abstract class TemporalAction<T extends IAgent> implements IAction<T> {

    // Private attributes
    private int elapsedTicks = 0;

    // Other methods

    /**
     *  Called before this action ticks for the 1st time (elapsedTicks = 0)
     * @param actioner
     *  The character involved in the action
     */
    public abstract void start(T actioner);

    /**
     *  Called every game tick until this action fails or succeeds
     * @param actioner
     *  The character involved in the action
     * @param elapsedTicks
     *  The amount of ticks that have elapsed since this action started
     * @return
     *  The status of the action
     */
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
