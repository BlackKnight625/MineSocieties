package ulisboa.tecnico.minesocieties.agents.npc;

import org.entityutils.entity.npc.player.AnimatedPlayerNPC;
import ulisboa.tecnico.agents.actions.IAction;
import ulisboa.tecnico.agents.actions.IActionVisitor;
import ulisboa.tecnico.agents.npc.IAgent;
import ulisboa.tecnico.agents.observation.IObservation;
import ulisboa.tecnico.agents.observation.IObserver;
import ulisboa.tecnico.agents.observation.ReceivedChatObservation;
import ulisboa.tecnico.agents.observation.WeatherChangeObservation;
import ulisboa.tecnico.minesocieties.agents.SocialCharacter;
import ulisboa.tecnico.minesocieties.agents.actions.IActionWithArguments;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.Idle;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.SendChatTo;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentState;
import ulisboa.tecnico.minesocieties.visitors.*;

import java.util.LinkedList;
import java.util.List;

public class SocialAgent extends SocialCharacter implements IAgent {

    // Private attributes

    private final AnimatedPlayerNPC npc;
    private AgentState state = new AgentState();
    private IAction<SocialAgent, IActionExplainerVisitor> currentAction = new Idle();

    // Constructors

    public SocialAgent(AnimatedPlayerNPC npc) {
        this.npc = npc;

        this.npc.setAlive(true);
    }

    // Getters and setters

    @Override
    public AnimatedPlayerNPC getAgent() {
        return npc;
    }

    public AgentState getState() {
        return state;
    }

    public void setState(AgentState state) {
        this.state = state;
    }

    // Observation methods

    @Override
    public void observeWeatherChange(WeatherChangeObservation observation) {

    }

    @Override
    public void receivedChatFrom(ReceivedChatObservation observation) {

    }

    @Override
    public void receivedAnyObservation(IObservation<IObserver> observation) {
        // This agent will prompt the LLM to know whether they should react to this observation
        observation.accept(this);


    }

    // Other methods

    public void chooseNewAction() {
        var possibleActions = getPossibleActions();

        if (possibleActions.isEmpty()) {
            // No actios may be taken by this agent
        } else {
            // TODO Ask ChatGPT to choose an action

        }
    }

    public void startNewAction(IAction<SocialAgent, IActionExplainerVisitor> newAction) {
        if (currentAction instanceof SendChatTo && !(newAction instanceof SendChatTo)) {
            // This agent has stopped a conversation. Time to reflect

            // TODO Make the agent reflect uppon the recent conversation
        }

        currentAction = newAction;

        newAction.act(this);
    }

    private StringBuilder addFullAgentDescription(StringBuilder builder) {
        addCurrentContext(builder);
        addCurrentAction(builder);

        return builder;
    }

    private StringBuilder addCurrentAction(StringBuilder builder) {
        CurrentActionExplainer currentActionExplainer = new CurrentActionExplainer();

        currentAction.accept(currentActionExplainer);

        return builder.append(currentActionExplainer.getLastExplanation());
    }

    private StringBuilder addPossibleActions(StringBuilder builder, List<IAction<SocialAgent, IActionExplainerVisitor>> possibleActions) {
        builder.append("What action should " + getName() + " choose? The coices are:\n");

        NextActionExplainer nextActionExplainer = new NextActionExplainer();
        ActionArgumentsExplainer actionArgumentsExplainer = new ActionArgumentsExplainer();

        int i = 0;

        for (var action : possibleActions) {
            builder.append(i);
            builder.append(") ");

            // Explaining the action

            action.accept(nextActionExplainer);

            builder.append(nextActionExplainer.getLastExplanation());
            builder.append(".");

            if (action instanceof IActionWithArguments actionWithArguments) {
                builder.append(" If you choose this, ");
                builder.append(actionWithArguments.acceptArgumentsExplainer(actionArgumentsExplainer, this));
            }

            builder.append(".\n");

            i++;
        }

        return builder;
    }

    private StringBuilder addCurrentContext(StringBuilder builder) {
        CurrentContextExplainer contextExplainer = new CurrentContextExplainer();

        return builder.append(contextExplainer.explainState(state));
    }

    public List<IAction<SocialAgent, IActionExplainerVisitor>> getPossibleActions() {
        List<IAction<SocialAgent, IActionExplainerVisitor>> possibleActions = new LinkedList<>();

        // Adding all actions that this agent can take
        possibleActions.add(new SendChatTo());

        // Removing actions that cannot be executed
        possibleActions.removeIf(action -> !action.canBeExecuted(this));

        return possibleActions;
    }

    @Override
    public void deleted() {

    }
}
