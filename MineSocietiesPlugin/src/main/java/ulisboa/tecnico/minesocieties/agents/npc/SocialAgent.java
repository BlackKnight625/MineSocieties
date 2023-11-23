package ulisboa.tecnico.minesocieties.agents.npc;

import org.entityutils.entity.npc.player.AnimatedPlayerNPC;
import org.jetbrains.annotations.Nullable;
import ulisboa.tecnico.agents.actions.ActionStatus;
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
import ulisboa.tecnico.minesocieties.agents.actions.exceptions.MalformedActionArgumentsException;
import ulisboa.tecnico.minesocieties.agents.actions.exceptions.MalformedActionChoiceException;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.ContinueCurrentAction;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.Idle;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.Thinking;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.SendChatTo;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentState;
import ulisboa.tecnico.minesocieties.agents.observation.ISocialObserver;
import ulisboa.tecnico.minesocieties.agents.observation.wrapped.SocialReceivedChatFromObservation;
import ulisboa.tecnico.minesocieties.agents.observation.wrapped.SocialWeatherChangeObservation;
import ulisboa.tecnico.minesocieties.visitors.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static ulisboa.tecnico.minesocieties.utils.PromptUtils.*;

public class SocialAgent extends SocialCharacter implements IAgent, ISocialObserver {

    // Private attributes

    private final AnimatedPlayerNPC npc;
    private AgentState state = new AgentState();
    private ISocialAction currentAction = new Idle();
    private final Lock actionChoosingLock = new ReentrantLock();

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
    public void observeWeatherChange(SocialWeatherChangeObservation observation) {
        receivedAnyObservation(observation);
    }

    @Override
    public void receivedChatFrom(SocialReceivedChatFromObservation observation) {
        receivedAnyObservation(observation);
    }

    public void receivedAnyObservation(IObservation<ISocialObserver> observation) {
        // This agent will prompt the LLM to know whether they should react to this observation
        chooseNewAction(observation);
    }

    // Other methods

    public void tick() {
        act();
    }

    /**
     *  Called every tick to make the agent continue its current action or choose a new action if they're finished with
     * their current one
     */
    public void act() {
        ActionStatus status = currentAction.act(this);

        if (status.isFinished()) {
            if (currentAction instanceof Thinking) {
                // Choosing a new action
                chooseNewAction(null);
            } else {
                // Start thinking about what to do next
                selectedNewAction(new Thinking("what to do next", 20));
            }
        }
    }


    @Override
    public void deploy() {
        this.npc.setAlive(true);
    }

    public void chooseNewAction(@Nullable IObservation<ISocialObserver> newlyObtainedObservation) {
        var possibleActions = getPossibleActions();

        if (possibleActions.isEmpty()) {
            // No actions may be taken by this agent
        } else {
            MineSocieties.getPlugin().getLLMManager().promptAsyncSupplyMessageAsync(
                    () -> getPromptForNewAction(possibleActions, newlyObtainedObservation),
                    response -> {
                try {
                    interpretNewActionSync(response, possibleActions);
                } catch (MalformedActionChoiceException | MalformedActionArgumentsException e) {
                    MineSocieties.getPlugin().getLogger().severe("Something went wrong while interpreting the LLM's reply to " +
                            "an Action Choice request. " + e.getMessage());
                }
            });
        }
    }

    public List<LLMMessage> getPromptForNewAction(List<ISocialAction> possibleActions, @Nullable IObservation<ISocialObserver> newlyObtainedObservation) {
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

    private void interpretNewActionSync(String actionReply, List<ISocialAction> possibleActions) throws MalformedActionChoiceException, MalformedActionArgumentsException {
        String[] replySections = actionReply.split("\\{");

        if (replySections.length == 0) {
            throw new MalformedActionChoiceException(actionReply, "Action choice is malformed: There's no curly brackets '{'.");
        }

        if (replySections.length != 3) {
            throw new MalformedActionChoiceException(actionReply, "Action choice does not contain 3 sections of curly brackets. " +
                    "It only contains " + replySections.length + ".");
        }

        // Extracting the action number
        int actionNumberEndIndex = replySections[0].indexOf('}');

        if (actionNumberEndIndex == -1) {
            throw new MalformedActionChoiceException(actionReply, "Action choice is malformed: There's no curly brackets '}'.");
        }

        int actionNumber;

        try {
            actionNumber = Integer.parseInt(replySections[0].substring(0, actionNumberEndIndex));
        } catch (NumberFormatException e) {
            throw new MalformedActionChoiceException(e, actionReply, "Could not read the Action choice number.");
        }

        actionNumber--; // The action number is the number of the action, not the index. As such, it must be decremented

        ISocialAction action;

        try {
            action = possibleActions.get(actionNumber);
        } catch (IndexOutOfBoundsException e) {
            NextActionExplainer nextActionExplainer = new NextActionExplainer();

            throw new MalformedActionChoiceException(e, actionReply, "The Action choice's number exceeds the possible action list's size of " +
                    possibleActions.size() + ": " + possibleActions.stream().map(a -> a.accept(nextActionExplainer)).toList() + ".");
        }

        if (action instanceof ContinueCurrentAction) {
            // This is a special action choice that means that the current action should not be modified.
            return;
        }

        if (action instanceof IActionWithArguments actionWithArguments) {
            // This action has arguments. They must be read
            int actionArgumentsEndIndex = replySections[1].indexOf('}');

            if (actionArgumentsEndIndex == -1) {
                throw new MalformedActionChoiceException(actionReply, "Action arguments are malformed: There's no curly brackets '}'.");
            }

            String arguments = replySections[1].substring(0, actionArgumentsEndIndex);
            ActionArgumentsExplainer actionArgumentsExplainer = new ActionArgumentsExplainer();

            actionWithArguments.acceptArgumentsInterpreter(actionArgumentsExplainer, arguments);
        }

        selectedNewAction(action);
    }

    public void selectedNewAction(ISocialAction newAction) {
        if (!(newAction instanceof Thinking) && !(newAction instanceof SendChatTo)) {
            // The agent is not thinking, and it's not chatting. Check if the agent should reflect uppon recent conversations

            reflectOnConversationsAsync();
        }

        actionChoosingLock.lock();

        try {
            currentAction.cancel();
            currentAction = newAction;
        } finally {
            actionChoosingLock.unlock();
        }
    }

    public void reflectOnConversationsSync() {
        if (state.getMemory().getConversations().entrySizes() != 0) {
            // There's some reflecting to do
            state.requestStateChangeSync(getPromptForConversationReflectingSync());
        }
    }

    public void reflectOnConversationsAsync() {
        if (state.getMemory().getConversations().entrySizes() != 0) {
            // There's some reflecting to do
            state.requestStateChangeAsync(this::getPromptForConversationReflectingSync);
        }
    }

    public List<LLMMessage> getPromptForConversationReflectingSync() {
        List<LLMMessage> messageList = new ArrayList<>(4);

        // Telling the model exactly what to do
        messageList.add(new LLMMessage(LLMRole.SYSTEM,
                        "You are a knowledge extractor AI. " +
                                "You will receive a person's description and a list of recent conversations inside brackets " +
                                "'<name>: {<description>}{<conversations>}'. " +
                                state.getStateFormat()
                )
        );

        // Giving an example of input to the model
        messageList.add(new LLMMessage(LLMRole.USER,
                        "Rafael: {Rafael is 22 years old. Their personality consists: ai-enthusiast, intelligent. Their current emotions are: " +
                                "relaxed, focused. Francisco likes Rafael's thesis. Opinions about Rui Prada: Rafael trusts him. " +
                                "Opinions about Francisco: Rafael likes hanging out with Francisco.}{" +
                                "Rui Prada told Rafael: {Hey, if you need any help with your thesis, let me know!}. " +
                                "Rafael told Rui Prada: {I'm struggling a bit with chapter 3, as I'm not sure what needs to be written there.}. " +
                                "Rui Prada told Rafael: {Well, let me look into the guidelines and I'll get back to you in a second. Also, don't forget " +
                                "to submit chapter 2 tonight, as the deadline is at 23h59}. "
                )
        );

        // Giving an example of output to the model
        messageList.add(new LLMMessage(LLMRole.ASSISTANT,
                PERSONALITIES_FORMAT_BEGIN + "{ai-enthusiast|intelligent}\n" +
                        EMOTIONS_FORMAT_BEGIN + "{stressed|focused}\n" +
                        REFLECTIONS_FORMAT_BEGIN + "{Francisco likes Rafael's thesis|Rafael needs to work faster on chapter 2|Rui Prada is offering " +
                        "Rafael help with writing his thesis}\n" +
                        OPINIONS_FORMAT_BEGIN + "{Rui Prada[Rafael trusts Rui Prada]|Francisco[Rafael likes hanging out with Francisco]}\n" +
                        SHORT_MEMORY_FORMAT_BEGIN + "{Rafael needs to submit chapter 2 tonight before 23h59|Rui Prada said he will help Rafael with chapter 3 in a second}\n" +
                        LONG_MEMORY_FORMAT_BEGIN + "{Rafael is struggling with chapter 3}\n" +
                        "Explanation: Nothing in the conversation suggests a personality change, as such, Rafael's personalities remain the same. " +
                        "Since Rafael was reminded that there's a deadline for tonight, it makes sense for Rafael to no longer be relaxed and instead " +
                        "be stressed. \"Francisco likes Rafael's thesis\" is an important reflection that should be kept, as it could be relevant in " +
                        "future conversations with Francisco. Rafael's trust in Rui Prada has to reason to be broken, so this opinion remains the same. " +
                        "The same applies to Rafael's opinion regarding Francisco. " +
                        "Rafael was reminded about the close deadline for chapter 2's submission, which will take place soon, hence it's considered short-term memory. " +
                        "Rui Prada said he will look into the guidelines and then help Rafael with chapter 3, which according to him, should be quick, as such, " +
                        "this should be part of Rafael's short-term memory. " +
                        "Rafael's struggles with chapter 3 is something that prevails until contested. As such, it's long-term memory. "
                )
        );

        // Giving it the desired input
        StringBuilder builder = new StringBuilder();

        builder.append(getName()).append(": {");

        addFullAgentDescription(builder).append("}{");
        builder.append(state.getMemory().getConversations().accept(new CurrentContextExplainer())).append("}");

        messageList.add(new LLMMessage(LLMRole.USER, builder.toString()));

        return messageList;
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
        possibleActions.add(new ContinueCurrentAction());

        // Removing actions that cannot be executed
        possibleActions.removeIf(action -> !action.canBeExecuted(this));

        return possibleActions;
    }

    @Override
    public void deleted() {

    }
}
