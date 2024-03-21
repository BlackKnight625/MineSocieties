package ulisboa.tecnico.minesocieties.agents.actions;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
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

    /**
     * @return
     *  Returns whether an agent executing this action can also execute micro actions such as
     * turning
     */
    default boolean canDoMicroActions() {
        return false;
    }

    /**
     * @return
     *  Returns whether this action should be remembered as an agent's past actions
     */
    default boolean shouldBeRemembered() {
        return true;
    }

    /**
     * @return
     *  Returns the text that an agent should display as what they're thinking about after executing this action
     */
    default String getThinkingText() {
        return "what to do next";
    }

    /**
     * @return
     *  Returns whether this action can be continued when the LLM is prompted for a new action. If true, then the LLM
     * will receive the option of continuing this action instead of choosing a new one.
     */
    default boolean canBeContinued() {
        return true;
    }

    /**
     *  Checks if an action of this kind could possibly be executed by anyone at the given Location.
     * This is used to check if an action of this kind can be associated with a SocialLocation.
     *  For example, a SocialLocation should not be able to have a InformativeGoFishing action associated with it if
     * there's no water nearby.
     * @param location
     *  The given location
     * @return
     *  A Pair holding true if an action of this kind could possibly be executed by anyone at the given Location or
     * holding false and a String with the reason why it can't be executed.
     */
    default Pair<Boolean, String> canBeExecutedInLocation(Location location) {
        return Pair.of(true, "");
    }
}
