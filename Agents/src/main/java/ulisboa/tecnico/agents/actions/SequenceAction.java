package ulisboa.tecnico.agents.actions;

import ulisboa.tecnico.agents.ICharacter;

public class SequenceAction<T extends ICharacter> implements IAction<T> {

    // Private attributes

    private final IAction<T>[] actions;
    private int currentActionIndex = 0;

    // Constructor

    public SequenceAction(IAction<T>... actions) {
        this.actions = actions;
    }

    // Other methods

    @Override
    public ActionStatus act(T actioner) {
        if (currentActionIndex > actions.length) {
            return ActionStatus.SUCCESS;
        }

        ActionStatus currentStatus = actions[currentActionIndex].act(actioner);

        if (currentStatus == ActionStatus.IN_PROGRESS) {
            // The current action is still in progress
            return ActionStatus.IN_PROGRESS;
        } else if (currentStatus == ActionStatus.SUCCESS) {
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
}