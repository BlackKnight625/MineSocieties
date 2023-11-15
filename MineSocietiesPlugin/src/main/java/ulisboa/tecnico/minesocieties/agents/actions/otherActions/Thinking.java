package ulisboa.tecnico.minesocieties.agents.actions.otherActions;

import ulisboa.tecnico.agents.actions.ActionStatus;
import ulisboa.tecnico.agents.actions.TemporalAction;
import ulisboa.tecnico.minesocieties.agents.actions.ISocialAction;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.visitors.IActionVisitor;

public class Thinking extends TemporalAction<SocialAgent> implements ISocialAction {

    // Private attributes

    private final String what;
    private final int ticks;

    // Constructors

    public Thinking(String what, int ticks) {
        this.what = what;
        this.ticks = ticks;
    }

    // Getters and setters

    public String getWhat() {
        return what;
    }

    public int getTicks() {
        return ticks;
    }

    // Other methods

    @Override
    public void start(SocialAgent actioner) {
        // Nothing to do
    }

    @Override
    public ActionStatus tick(SocialAgent actioner, int elapsedTicks) {
        if (elapsedTicks == ticks) {
            return ActionStatus.SUCCESS;
        } else {
            return ActionStatus.IN_PROGRESS;
        }
    }

    @Override
    public String accept(IActionVisitor visitor) {
        return visitor.visitThinking(this);
    }
}
