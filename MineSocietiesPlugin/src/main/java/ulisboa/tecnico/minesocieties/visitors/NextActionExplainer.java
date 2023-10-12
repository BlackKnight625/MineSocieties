package ulisboa.tecnico.minesocieties.visitors;

import ulisboa.tecnico.minesocieties.agents.actions.otherActions.Idle;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.InformativeGoTo;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.WaitFor;

/**
 *  Visitor that turns actions into natural language explanations of what the action
 * will make an agent do.
 *  For example, if an agent's possible next action is "Go to", then this visitor will explain
 * that the agent will go to a specific destination.
 */
public class NextActionExplainer implements IActionExplainerVisitor {
    @Override
    public String explainGoTo(InformativeGoTo informativeGoTo) {
        return "Go to " + informativeGoTo.getDestinationDescription();
    }

    @Override
    public String explainIdle(Idle idle) {
        return "Go idle";
    }

    @Override
    public String explainWaitFor(WaitFor waitFor) {
        return "Wait for " + waitFor.getWhat();
    }
}
