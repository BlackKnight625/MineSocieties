package ulisboa.tecnico.minesocieties.visitors;

import ulisboa.tecnico.agents.actions.otherActions.GoTo;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.*;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.SendChatTo;

/**
 *  Visitor that turns actions into natural language explanations of what the action
 * will make an agent do.
 *  For example, if an agent's possible next action is "Go to", then this visitor will explain
 * that the agent will go to a specific destination.
 */
public class NextActionExplainer implements IActionVisitor {

    @Override
    public String visitGoTo(InformativeGoTo informativeGoTo) {
        return "Go to " + informativeGoTo.getDestinationDescription();
    }

    @Override
    public String visitIdle(Idle idle) {
        return "Go idle";
    }

    @Override
    public String visitWaitFor(WaitFor waitFor) {
        return "Wait for " + waitFor.getWhat();
    }

    @Override
    public String visitSendChatTo(SendChatTo sendChatTo) {
        return "Engage in conversation";
    }

    @Override
    public String visitContinueCurrentAction(ContinueCurrentAction continueCurrentAction) {
        return "Continue doing what they're currently doing";
    }

    @Override
    public String visitThinking(Thinking thinking) {
        return "Think about " + thinking.getWhat();
    }
}
