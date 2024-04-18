package ulisboa.tecnico.minesocieties.visitors;

import ulisboa.tecnico.minesocieties.agents.actions.jobActions.InformativeGoFishing;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.*;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.GiveItemTo;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.SendChatTo;

/**
 *  Visitor that turns actions into natural language explanations of what they just finished doing.
 *  For example, if an agent's current action is "Go to", then this visitor will explain
 * that the agent just arrived at some location.
 */
public class PastActionExplainer implements IActionVisitor {

    @Override
    public String visitGoTo(InformativeGoTo informativeGoTo) {
        return "arrived at " + informativeGoTo.getDestinationDescription();
    }

    @Override
    public String visitIdle(Idle idle) {
        return "stopped being idle";
    }

    @Override
    public String visitWaitFor(WaitFor waitFor) {
        return "were done waiting for " + waitFor.getWhat();
    }

    @Override
    public String visitSendChatTo(SendChatTo sendChatTo) {
        // No need to expose the contents of the chat since agents remember their recent conversations
        return "chatted with " + sendChatTo.getReceiver().getName();
    }

    @Override
    public String visitContinueCurrentAction(ContinueCurrentAction continueCurrentAction) {
        return ""; // Special Action that is never an Agent's current action
    }

    @Override
    public String visitThinking(Thinking thinking) {
        return "thought about " + thinking.getWhat() + " for " + (thinking.getThinkingTicks() / 20) + " seconds";
    }

    @Override
    public String visitGoFishing(InformativeGoFishing informativeGoFishing) {
        return "went fishing";
    }

    @Override
    public String visitGiveItemTo(GiveItemTo giveItemTo) {
        return "gave " + giveItemTo.getItem().getAmount() + " " +
                giveItemTo.getItem().getType().toString().replace('_', ' ').toLowerCase() + " to " +
                giveItemTo.getReceiver().getName();
    }
}
