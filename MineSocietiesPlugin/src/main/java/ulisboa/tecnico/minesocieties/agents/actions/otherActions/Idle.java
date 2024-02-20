package ulisboa.tecnico.minesocieties.agents.actions.otherActions;

import ulisboa.tecnico.agents.actions.ActionStatus;
import ulisboa.tecnico.minesocieties.agents.actions.ISocialAction;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.visitors.IActionVisitor;

public class Idle implements ISocialAction {
    @Override
    public ActionStatus act(SocialAgent actioner) {
        return ActionStatus.IN_PROGRESS;
    }

    @Override
    public String accept(IActionVisitor visitor) {
        return visitor.visitIdle(this);
    }

    @Override
    public boolean canDoMicroActions() {
        return true;
    }

    @Override
    public boolean canBeContinued() {
        return false; // False since an Idle action is always provided to the list of possible actions
    }
}
