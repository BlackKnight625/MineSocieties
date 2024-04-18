package ulisboa.tecnico.minesocieties.visitors;

import ulisboa.tecnico.minesocieties.agents.actions.jobActions.InformativeGoFishing;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.ContinueCurrentAction;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.Idle;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.InformativeGoTo;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.Thinking;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.WaitFor;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.GiveItemTo;
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
        String explanation = "Go to " + informativeGoTo.getDestinationDescription();
        var possibleActions = informativeGoTo.getPossibleActionsAtDestination();

        if (possibleActions.isEmpty()) {
            return explanation;
        } else {
            // Adding all possible actions that can be executed at the destination
            StringBuilder builder = new StringBuilder(explanation);

            builder.append(", where they could ");

            for (int i = 0; i < possibleActions.size(); i++) {
                builder.append(possibleActions.get(i).accept(this));

                if (i < possibleActions.size() - 1) {
                    builder.append(" or ");
                }
            }

            return builder.toString();
        }
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
        // Special action. Will explain that the agent may continue doing the current action
        return "Continue " + continueCurrentAction.getCurrentAction().accept(new CurrentActionExplainer());
    }

    @Override
    public String visitThinking(Thinking thinking) {
        return "Think about " + thinking.getWhat();
    }

    @Override
    public String visitGoFishing(InformativeGoFishing informativeGoFishing) {
        return "Go fishing";
    }

    @Override
    public String visitGiveItemTo(GiveItemTo giveItemTo) {
        return "Give an item from their inventory to someone";
    }
}
