package ulisboa.tecnico.minesocieties.agents.actions.otherActions;

import ulisboa.tecnico.agents.actions.ActionStatus;
import ulisboa.tecnico.agents.actions.IAction;
import ulisboa.tecnico.minesocieties.agents.actions.IExplainableAction;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.visitors.IActionExplainerVisitor;

public class Idle implements IAction<SocialAgent>, IExplainableAction {
    @Override
    public ActionStatus act(SocialAgent actioner) {
        return ActionStatus.IN_PROGRESS;
    }

    @Override
    public String accept(IActionExplainerVisitor visitor) {
        return visitor.explainIdle(this);
    }
}
