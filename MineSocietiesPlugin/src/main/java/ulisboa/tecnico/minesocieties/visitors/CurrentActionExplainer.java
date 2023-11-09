package ulisboa.tecnico.minesocieties.visitors;

import ulisboa.tecnico.agents.actions.otherActions.GoTo;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.Idle;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.InformativeGoTo;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.WaitFor;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.SendChatTo;

/**
 *  Visitor that turns actions into natural language explanations of what the action
 * is making an agent do at this current moment.
 *  For example, if an agent's current action is "Go to", then this visitor will explain
 * that the agent is currently going to a specific destination.
 */
public class CurrentActionExplainer implements IActionExplainerVisitor {

    // Private attributes

    private String lastExplanation = "";

    // Getters and setters

    public String getLastExplanation() {
        return lastExplanation;
    }

    // Other methods


    @Override
    public void visitGoTo(GoTo goTo) {
        // Do nothing. GoTo is wrapped by InformativeGoTo for MineSocieties
    }

    @Override
    public void visitGoTo(InformativeGoTo informativeGoTo) {
        lastExplanation = "going to " + informativeGoTo.getDestinationDescription();
    }

    @Override
    public void visitIdle(Idle idle) {
        lastExplanation = "idle";
    }

    @Override
    public void visitWaitFor(WaitFor waitFor) {
        lastExplanation = "waiting for " + waitFor.getWhat();
    }

    @Override
    public void visitSendChatTo(SendChatTo sendChatTo) {
        lastExplanation = "is chatting with " + sendChatTo.getReceiver().getName();
    }
}
