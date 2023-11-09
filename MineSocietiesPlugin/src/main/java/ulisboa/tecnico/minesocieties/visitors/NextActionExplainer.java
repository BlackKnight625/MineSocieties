package ulisboa.tecnico.minesocieties.visitors;

import ulisboa.tecnico.agents.actions.otherActions.GoTo;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.Idle;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.InformativeGoTo;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.WaitFor;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.SendChatTo;

/**
 *  Visitor that turns actions into natural language explanations of what the action
 * will make an agent do.
 *  For example, if an agent's possible next action is "Go to", then this visitor will explain
 * that the agent will go to a specific destination.
 */
public class NextActionExplainer implements IActionExplainerVisitor {

    // Private attributes

    private String lastExplanation;

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
        lastExplanation = "Go to " + informativeGoTo.getDestinationDescription();
    }

    @Override
    public void visitIdle(Idle idle) {
        lastExplanation = "Go idle";
    }

    @Override
    public void visitWaitFor(WaitFor waitFor) {
        lastExplanation = "Wait for " + waitFor.getWhat();
    }

    @Override
    public void visitSendChatTo(SendChatTo sendChatTo) {
        lastExplanation = "Engage in conversation";
    }
}
