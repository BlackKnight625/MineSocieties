package ulisboa.tecnico.minesocieties.visitors;

import ulisboa.tecnico.minesocieties.agents.actions.otherActions.Idle;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.InformativeGoTo;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.WaitFor;

/**
 *  Visitor that turns actions into natural language explanations of what the action
 * is making an agent do at this current moment.
 *  For example, if an agent's current action is "Go to", then this visitor will explain
 * that the agent is currently going to a specific destination.
 */
public class CurrentActionExplainer implements IActionExplainerVisitor {
    @Override
    public String explainGoTo(InformativeGoTo informativeGoTo) {
        return "going to " + informativeGoTo.getDestinationDescription();
    }

    @Override
    public String explainIdle(Idle idle) {
        return "idle";
    }

    @Override
    public String explainWaitFor(WaitFor waitFor) {
        return "waiting for " + waitFor.getWhat();
    }
}
