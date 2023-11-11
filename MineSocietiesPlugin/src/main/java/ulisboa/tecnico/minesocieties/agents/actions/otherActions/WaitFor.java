package ulisboa.tecnico.minesocieties.agents.actions.otherActions;

import ulisboa.tecnico.agents.actions.ActionStatus;
import ulisboa.tecnico.minesocieties.agents.actions.ISocialAction;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.visitors.IActionVisitor;

public class WaitFor implements ISocialAction {

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
    public String accept(IActionVisitor visitor) {
        return visitor.visitWaitFor(this);
    }
}
