package ulisboa.tecnico.minesocieties.agents.actions.otherActions;

import ulisboa.tecnico.agents.actions.ActionStatus;
import ulisboa.tecnico.minesocieties.agents.actions.ISocialAction;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.visitors.IActionVisitor;

public class ContinueCurrentAction implements ISocialAction {

    // Private attributes

    private final ISocialAction currentAction;

    // Constructors

    public ContinueCurrentAction(ISocialAction currentAction) {
        this.currentAction = currentAction;
    }

    // Getters and setters

    public ISocialAction getCurrentAction() {
        return this.currentAction;
    }

    // Other methods

    @Override
    public ActionStatus act(SocialAgent actioner) {
        return ActionStatus.SUCCESS;
    }

    @Override
    public String accept(IActionVisitor visitor) {
        return visitor.visitContinueCurrentAction(this);
    }
}
