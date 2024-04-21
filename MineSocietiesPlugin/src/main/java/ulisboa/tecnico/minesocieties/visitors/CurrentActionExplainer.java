package ulisboa.tecnico.minesocieties.visitors;

import ulisboa.tecnico.agents.actions.otherActions.GoTo;
import ulisboa.tecnico.minesocieties.agents.actions.jobActions.InformativeGoFishing;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.*;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.GiveItemTo;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.SendChatTo;
import ulisboa.tecnico.minesocieties.utils.StringUtils;

/**
 *  Visitor that turns actions into natural language explanations of what the action
 * is making an agent do at this current moment.
 *  For example, if an agent's current action is "Go to", then this visitor will explain
 * that the agent is currently going to a specific destination.
 */
public class CurrentActionExplainer implements IActionVisitor {

    @Override
    public String visitGoTo(InformativeGoTo informativeGoTo) {
        return "going to " + informativeGoTo.getDestinationDescription();
    }

    @Override
    public String visitIdle(Idle idle) {
        return "doing nothing";
    }

    @Override
    public String visitWaitFor(WaitFor waitFor) {
        return "waiting for " + waitFor.getWhat();
    }

    @Override
    public String visitSendChatTo(SendChatTo sendChatTo) {
        return "chatting with " + sendChatTo.getReceiver().getName();
    }

    @Override
    public String visitContinueCurrentAction(ContinueCurrentAction continueCurrentAction) {
        return ""; // Special Action that is never an Agent's current action
    }

    @Override
    public String visitThinking(Thinking thinking) {
        return "thinking about " + thinking.getWhat();
    }

    @Override
    public String visitGoFishing(InformativeGoFishing informativeGoFishing) {
        return "fishing";
    }

    @Override
    public String visitGiveItemTo(GiveItemTo giveItemTo) {
        return "giving " + StringUtils.itemToAmountAndName(giveItemTo.getItem()) + " to " +
                giveItemTo.getReceiver().getName();
    }
}
