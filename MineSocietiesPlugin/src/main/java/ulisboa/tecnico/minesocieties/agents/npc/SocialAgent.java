package ulisboa.tecnico.minesocieties.agents.npc;

import org.entityutils.entity.npc.player.AnimatedPlayerNPC;
import org.jetbrains.annotations.Nullable;
import ulisboa.tecnico.agents.actions.IAction;
import ulisboa.tecnico.agents.npc.IAgent;
import ulisboa.tecnico.agents.observation.IObservation;
import ulisboa.tecnico.agents.observation.IObserver;
import ulisboa.tecnico.agents.observation.ReceivedChatObservation;
import ulisboa.tecnico.agents.observation.WeatherChangeObservation;
import ulisboa.tecnico.llms.LLMMessage;
import ulisboa.tecnico.llms.LLMRole;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.SocialCharacter;
import ulisboa.tecnico.minesocieties.agents.actions.IActionWithArguments;
import ulisboa.tecnico.minesocieties.agents.actions.ISocialAction;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.Idle;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.SendChatTo;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentState;
import ulisboa.tecnico.minesocieties.visitors.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SocialAgent extends SocialCharacter implements IAgent {

    // Private attributes

    private final AnimatedPlayerNPC npc;
    private AgentState state = new AgentState();
    private ISocialAction currentAction = new Idle();

    // Constructors

    public SocialAgent(AnimatedPlayerNPC npc) {
        this.npc = npc;
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

        chooseNewAction(observation);
    }

    // Other methods


    @Override
    public void deploy() {
        this.npc.setAlive(true);
    }

    public void chooseNewAction(@Nullable IObservation<IObserver> newlyObtainedObservation) {
        var possibleActions = getPossibleActions();

        if (possibleActions.isEmpty()) {
            // No actions may be taken by this agent
        } else {
            // TODO Ask ChatGPT to choose an action
            var messages = getPromptForNewAction(possibleActions, newlyObtainedObservation);

            MineSocieties.getPlugin().getLLMManager().promptAsync(messages, response -> interpretNewAction(response, possibleActions));
        }
    }

    public List<LLMMessage> getPromptForNewAction(List<ISocialAction> possibleActions, @Nullable IObservation<IObserver> newlyObtainedObservation) {
        List<LLMMessage> messageList = new ArrayList<>(4);

        // Telling the model exactly what to do
        messageList.add(new LLMMessage(LLMRole.SYSTEM,
                "You are a decision making AI for " + getName() + ". " +
                        "You will receive their description and a list of possible actions. " +
                        "You must choose the action that is the most appropriate for " + getName() +
                        " given the sittuation they find themselves in. Write down your choice as " +
                        "{<number of action>}{<optional arguments, if none leave empty}{<the reasoning for your choice>}"
                )
        );

        // Giving an example of input to the model
        messageList.add(new LLMMessage(LLMRole.USER,
                "{Rafael is 22 years old. Their personality consists: ai-enthusiast, intelligent. Their current emotions are: " +
                        "relaxed, focused. Francisco likes Rafael's thesis. Rui Prada is helping Rafael writing his thesis. " +
                        "Rui Prada just said 'Hi Rafael! Do you need more help with Chapter 4?'} " +
                        "{Actions:\n1) Engage in conversation. If you choose this, write the name of the person who should receive the message " +
                        "and then the message in this format: name|message. The possible people to chat with are {Francisco, Rui Prada}.\n" +
                        "2) Go home.\n}"
                )
        );

        // Giving an example of output to the model
        messageList.add(new LLMMessage(LLMRole.ASSISTANT,
                "{1}{Rui Prada|Thanks for offering, but for now I'm all good. I'll let you know if I need help!}" +
                        "{Since Rafael is focused, going home now will break said focus. Rui Prada just started a conversation with Rafael, " +
                        "so it's logical that Rafael would reply to Rui Prada, addressing Rui Prada's offer for help. There's no indication that " +
                        "Rafael is struggling or needs help, as such, Rafael politely refuses Rui Prada's help.}"
                )
        );

        // Giving it the desired input
        StringBuilder builder = new StringBuilder("{");

        addFullAgentDescription(builder).append("\n");

        if (newlyObtainedObservation != null) {
            ObservationExplainerVisitor observationExplainer = new ObservationExplainerVisitor();

            newlyObtainedObservation.accept(observationExplainer);

            builder.append(observationExplainer.getLastExplanation());
        }

        builder.append("} {Actions:\n");

        addPossibleActions(builder, possibleActions).append("}");

        messageList.add(new LLMMessage(LLMRole.USER, builder.toString()));

        return messageList;
    }

    private void interpretNewAction(String actionReply, List<ISocialAction> possibleActions) {

    }

    public void selectedNewAction(ISocialAction newAction) {
        if (currentAction instanceof SendChatTo && !(newAction instanceof SendChatTo)) {
            // This agent has stopped a conversation. Time to reflect

            // TODO Make the agent reflect uppon the recent conversation
        }

        if (newAction.equals(currentAction)) {
            // The new action is the same as the current one. Do nothing, as if the current task was simply resumed
        } else {
            currentAction.cancel();
            currentAction = newAction;
        }
    }

    private StringBuilder addFullAgentDescription(StringBuilder builder) {
        addCurrentContext(builder);
        builder.append(" ");
        addCurrentAction(builder);

        return builder;
    }

    private StringBuilder addCurrentAction(StringBuilder builder) {
        CurrentActionExplainer currentActionExplainer = new CurrentActionExplainer();

        return builder
                .append(getName())
                .append(" is currently ")
                .append(currentAction.accept(currentActionExplainer))
                .append(".");
    }

    private StringBuilder addPossibleActions(StringBuilder builder, List<ISocialAction> possibleActions) {
        NextActionExplainer nextActionExplainer = new NextActionExplainer();
        ActionArgumentsExplainer actionArgumentsExplainer = new ActionArgumentsExplainer();

        int i = 1;

        for (var action : possibleActions) {
            builder.append(i);
            builder.append(") ");

            // Explaining the action
            builder.append(action.accept(nextActionExplainer));

            if (action instanceof IActionWithArguments actionWithArguments) {
                builder.append(". If you choose this, ");
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

    public List<ISocialAction> getPossibleActions() {
        List<ISocialAction> possibleActions = new LinkedList<>();

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
