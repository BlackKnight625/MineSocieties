package ulisboa.tecnico.minesocieties.agents.actions;

import ulisboa.tecnico.agents.actions.IAction;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.visitors.IActionVisitor;

public interface ISocialAction extends IAction<SocialAgent> {

    String accept(IActionVisitor visitor);

    /**
     * @return
     *  Returns how long (in ticks) an agent should think for after completing this action.
     *  20 by default (1 second)
     */
    default int getThinkingTicks() {
        return 20;
    }
}
