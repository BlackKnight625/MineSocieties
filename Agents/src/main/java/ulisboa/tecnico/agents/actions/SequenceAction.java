package ulisboa.tecnico.agents.actions;

import ulisboa.tecnico.agents.npc.IAgent;

public abstract class SequenceAction<T extends IAgent> implements IAction<T> {

    // Private attributes

    private final IAction<T>[] actions;
    private int currentActionIndex = 0;
    private ActionStatus lastStatus = ActionStatus.IN_PROGRESS;

    // Constructor

    public SequenceAction(IAction<T>... actions) {
        assert actions.length != 0 : "A sequence action must have at least one action.";

        this.actions = actions;
    }

    // Other methods

    @Override
    public ActionStatus act(T actioner) {
        if (currentActionIndex > actions.length) {
            lastStatus = ActionStatus.SUCCESS;

            return ActionStatus.SUCCESS;
        }

        lastStatus = actions[currentActionIndex].act(actioner);

        if (lastStatus == ActionStatus.IN_PROGRESS) {
            // The current action is still in progress
            return ActionStatus.IN_PROGRESS;
        } else if (lastStatus == ActionStatus.SUCCESS) {
            // The current action was successful. Moving onto the next one

            currentActionIndex++;

            if (currentActionIndex > actions.length) {
                // That was the last action in the sequence. Success
                return ActionStatus.SUCCESS;
            } else {
                // There's still more actions in the sequence
                return ActionStatus.IN_PROGRESS;
            }
        } else {
            // The current action failed. The rest of the sequence will not be triggered
            return ActionStatus.FAILURE;
        }
    }

    @Override
    public void cancel(T actioner) {
        if (!lastStatus.isFinished()) {
            // Cancelling the current action
            actions[currentActionIndex].cancel(actioner);
        }
    }
}
