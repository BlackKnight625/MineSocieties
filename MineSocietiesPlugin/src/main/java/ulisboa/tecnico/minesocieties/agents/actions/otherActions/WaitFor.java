package ulisboa.tecnico.minesocieties.agents.actions.otherActions;

import ulisboa.tecnico.agents.actions.ActionStatus;
import ulisboa.tecnico.agents.actions.IAction;
import ulisboa.tecnico.minesocieties.agents.actions.IExplainableAction;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.visitors.IActionExplainerVisitor;

public class WaitFor implements IAction<SocialAgent, IActionExplainerVisitor>, IExplainableAction {

    // Private attributes

    private final String what;

    // Constructors

    public WaitFor(String what) {
        this.what = what;
    }

    // Getters and setters

    public String getWhat() {
        return what;
    }

    // Other methods

    @Override
    public ActionStatus act(SocialAgent actioner) {
        return ActionStatus.IN_PROGRESS;
    }

    @Override
    public void accept(IActionExplainerVisitor visitor) {
        visitor.visitWaitFor(this);
    }
}
