package ulisboa.tecnico.agents.actions;

import ulisboa.tecnico.agents.npc.IAgent;

public interface IAction<T extends IAgent> {

    ActionStatus act(T actioner);

    /**
     *  Called when this actions should stop being executed.
     *  Some action implementations might use external algorithms that need to be cancelled before the action
     * is complete.
     *  An action can be cancelled without ever being started.
     */
    default void cancel() {

    }

    /**
     *  Called before this action gets executed to enquire about its ability to be executed.
     * @return
     *  True if this action can be started. False if otherwise.
     */
    default boolean canBeExecuted(T actioner) {
        return true;
    }
}
