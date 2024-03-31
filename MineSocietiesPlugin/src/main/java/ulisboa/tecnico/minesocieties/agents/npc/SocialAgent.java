package ulisboa.tecnico.minesocieties.agents.npc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.entityutils.entity.npc.player.AnimatedPlayerNPC;
import org.jetbrains.annotations.Nullable;
import ulisboa.tecnico.agents.actions.ActionStatus;
import ulisboa.tecnico.agents.npc.IAgent;
import ulisboa.tecnico.agents.observation.IObservation;
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
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.InformativeGoTo;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.Thinking;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.SendChatTo;
import ulisboa.tecnico.minesocieties.agents.location.LocationReference;
import ulisboa.tecnico.minesocieties.agents.location.SharedAccess;
import ulisboa.tecnico.minesocieties.agents.location.SocialLocation;
import ulisboa.tecnico.minesocieties.agents.npc.state.*;
import ulisboa.tecnico.minesocieties.agents.observation.ISocialObserver;
import ulisboa.tecnico.minesocieties.agents.observation.wrapped.SocialReceivedChatFromObservation;
import ulisboa.tecnico.minesocieties.agents.observation.wrapped.SocialWeatherChangeObservation;
import ulisboa.tecnico.minesocieties.visitors.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static ulisboa.tecnico.minesocieties.utils.PromptUtils.*;

public class SocialAgent extends SocialCharacter implements IAgent, ISocialObserver {

    // Private attributes

    private final AnimatedPlayerNPC npc;
    private AgentState state;
    private ISocialAction currentAction = new Idle();
    private ActionStatus currentActionStatus = ActionStatus.SUCCESS;
    private MessageDisplay messageDisplay = new MessageDisplay(this);
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

    public MessageDisplay getMessageDisplay() {
        return messageDisplay;
    }

    public ISocialAction getCurrentAction() {
        return currentAction;
    }

    // Observation methods

    @Override
    public void observeWeatherChange(SocialWeatherChangeObservation observation) {
        receivedAnyObservation(observation);

        // Making the agent look up, in case the current actions allows so
        if (currentAction.canDoMicroActions()) {
            npc.setDirection(npc.getData().getYaw(), -45f);
        }
    }

    @Override
    public void receivedChatFrom(SocialReceivedChatFromObservation observation) {
        receivedAnyObservation(observation);

        Conversation conversation = new Conversation(observation, this);

        // Adding this conversation to the agent's memory
        state.getMemory().getConversations().addMemorySection(conversation);

        // Making the agent look at the speaker, in case the current action allows so
        if (currentAction.canDoMicroActions()) {
            npc.lookAt(observation.getObservation().getFrom().getLocation());
        }
    }

    public void receivedAnyObservation(IObservation<ISocialObserver> observation) {
        // This agent will prompt the LLM to know whether they should react to this observation
        chooseNewAction(observation);
    }

    // Other methods

    public UUID getSkin() {
        UUID skinUuid = npc.getData().getSkin();

        if (skinUuid == null) {
            return getUUID();
        } else {
            return skinUuid;
        }
    }

    public void tick() {
        act();

        messageDisplay.tick();

        // Even thought the npc instance knows its location, it must be updated in the AgentState so that it can be stored
        // in the agent files
        state.updateCurrentLocation(npc);
    }

    /**
     *  Called every tick to make the agent continue its current action or choose a new action if they're finished with
     * their current one
     */
    public void act() {
        currentActionStatus = currentAction.act(this);

        if (currentActionStatus.isFinished()) {
            if (currentAction instanceof Thinking) {
                // Choosing a new action
                chooseNewAction(null);
            } else {
                // Start thinking about what to do next
                selectedNewActionSync(new Thinking(currentAction.getThinkingText(), currentAction.getThinkingTicks()));
            }
        }
    }

    public void setNewHomeLocation() {
        SharedAccess access = new SharedAccess();
        SocialLocation newHome = new SocialLocation(getLocation(), getName() + "'s home", access);

        state.getMemory().setHome(newHome.toReference());

        // Adding this agent to the referenced agents after setting the home so that this lcoation may know that it's special
        access.addAgent(toReference());

        newHome.setGuiMaterial(Material.RED_BED);

        MineSocieties.getPlugin().getLocationsManager().addLocation(newHome);
        MineSocieties.getPlugin().getLocationsManager().saveAsync(newHome);

        state.markDirty();
    }


    @Override
    public void deploy() {
        npc.setAlive(true);

        if (state == null) {
            // It's the 1st time ever that this agent is being deployed. Giving it an initial state
            state = new AgentState(
                    getUUID(),
                    new AgentPersona(getName(), Instant.ofEpochSecond(
                            LocalDateTime.of(2000, Month.DECEMBER, 5, 12, 0).toEpochSecond(ZoneOffset.UTC)
                    )) // They all have my birthday by default, toot toot!
            );

            // Giving the agent a home
            setNewHomeLocation();

            // Must add public locations to the agent
            for (SocialLocation socialLocation : MineSocieties.getPlugin().getLocationsManager().getAllLocations()) {
                if (!socialLocation.getAccess().isRestricted()) {
                    state.getMemory().getKnownLocations().addMemorySection(socialLocation.toReference());
                }
            }
        }

        messageDisplay.initialize();
    }

    public void chooseNewAction(@Nullable IObservation<ISocialObserver> newlyObtainedObservation) {
        if (MineSocieties.getPlugin().agentsCanChooseActions()) {
            var possibleActions = getPossibleActions();

            if (possibleActions.isEmpty()) {
                // No actions may be taken by this agent
                MineSocieties.getPlugin().getLogger().warning("Agent " + getName() + " doesn't have any possible actions to choose from.");
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
        } else {
            // Agents are currently now allowed to query the LLM for new actions.
            // Making the agent idle for 5 seconds. After those 5 seconds, if the agent still can't choose a new action, it will
            // continue being idle
            selectedNewActionSync(new Thinking("what to do next", 100));
        }
    }

    public List<LLMMessage> getPromptForNewAction(List<ISocialAction> possibleActions, @Nullable IObservation<ISocialObserver> newlyObtainedObservation) {
        List<LLMMessage> messageList = new ArrayList<>(4);

        boolean showThoughts = MineSocieties.getPlugin().showThoughts();

        // Telling the model exactly what to do
        messageList.add(new LLMMessage(LLMRole.SYSTEM,
                "You are a decision making AI. " +
                        "You will receive the description of an NPC and a list of possible actions. " +
                        "You must choose the action that is the most appropriate for them " +
                        "given the situation they find themselves in. Write down your choice as " +
                        "Action{<number of action>}{<optional arguments, if none leave empty}{<the reasoning for your choice>}" +
                        (showThoughts ? "{<the NPC's thought process, short, 1st person>}" : "")
                )
        );

        // Giving an example of input to the model
        messageList.add(new LLMMessage(LLMRole.USER,
                "{Rafael is 22 years old. Their personality consists: ai-enthusiast, intelligent. Their current emotions are: " +
                        "relaxed, focused. Francisco likes Rafael's thesis. Rui Prada is helping Rafael writing his thesis. " +
                        "Rui Prada just said 'Hi Rafael! Do you need more help with Chapter 4?'} " +
                        "{Actions:\nAction 1) Engage in conversation. If you choose this, write the name of the person who should receive the message, then the message and then " +
                        "whether it makes sense for Rafael to wait for a reply (a 'yes' or 'no') in this format: name|message|wait_for_reply. The " +
                        "possible people to chat with are {Francisco, Rui Prada}\n" +
                        "Action 2) Go home.\n}"
                )
        );

        // Giving an example of output to the model
        messageList.add(new LLMMessage(LLMRole.ASSISTANT,
                "Action{1}{Rui Prada|Thanks for offering, but for now I'm all good. I'll let you know if I need help!|yes}" +
                        "{Since Rafael is focused, going home now will break said focus. Rui Prada just started a conversation with Rafael, " +
                        "so it's logical that Rafael would reply to Rui Prada, addressing Rui Prada's offer for help. There's no indication that " +
                        "Rafael is struggling or needs help, as such, Rafael politely refuses Rui Prada's help. Since Rafael is replying to Rui Prada's question, " +
                        "it makes sense for Rafael to wait for a reply as Rui Prada may want to say goodbye.}" +
                        (showThoughts ? "{I'm going to reply to Rui Prada thanking him for the offer and denying his help since I'm currently not " +
                                "struggling. I should wait for a reply in case Rui Prada wants to say goodbye.}" : "")
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
        boolean showThoughts = MineSocieties.getPlugin().showThoughts();
        String thoughtProcess = null;

        if (replySections.length == 0) {
            throw new MalformedActionChoiceException(actionReply, "Action choice is malformed: There's no curly brackets '{'.");
        }

        if (replySections.length < 4 /*It's 4 due to the 1st useless section behind the '{'*/) {
            throw new MalformedActionChoiceException(actionReply, "Action choice does not contain 3 sections of curly brackets. " +
                    "It only contains " + replySections.length + ".");
        }

        if (showThoughts && replySections.length < 5) {
            MineSocieties.getPlugin().getLogger().warning("The LLM's reply to an Action Choice request does not contain the NPC's thought process. " +
                    "The reply was: " + actionReply);
        } else {
            thoughtProcess = replySections[4].substring(0, replySections[4].length() - 1); // Removing the last '}'
        }

        // Extracting the action number
        int actionNumberEndIndex = replySections[1].indexOf('}');

        if (actionNumberEndIndex == -1) {
            throw new MalformedActionChoiceException(actionReply, "Action choice is malformed: There's no curly brackets '}'.");
        }

        int actionNumber;

        try {
            actionNumber = Integer.parseInt(replySections[1].substring(0, actionNumberEndIndex));
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

        if (showThoughts) {
            // Displaying the agent's though process on their head
            messageDisplay.displayMessage(new Message(MineSocieties.getPlugin().getShowThoughtsTicks(),
                    Component.text("\uD83E\uDD14").color(TextColor.color(225, 217, 64)) // 🤔
                            .append(Component.text(" " + thoughtProcess).color(TextColor.color(123, 124, 73)))
            ));
        }

        if (action instanceof ContinueCurrentAction) {
            // This is a special action choice that means that the current action should not be modified.
            return;
        }

        if (action instanceof IActionWithArguments actionWithArguments) {
            // This action has arguments. They must be read
            int actionArgumentsEndIndex = replySections[2].indexOf('}');

            if (actionArgumentsEndIndex == -1) {
                throw new MalformedActionChoiceException(actionReply, "Action arguments are malformed: There's no curly brackets '}'.");
            }

            String arguments = replySections[2].substring(0, actionArgumentsEndIndex);
            ActionArgumentsExplainer actionArgumentsExplainer = new ActionArgumentsExplainer();

            actionWithArguments.acceptArgumentsInterpreter(actionArgumentsExplainer, arguments);
        }

        selectedNewActionSync(action);
    }

    public void selectedNewActionSync(ISocialAction newAction) {
        if (!(newAction instanceof Thinking) && !(newAction instanceof SendChatTo)) {
            // The agent is not thinking, and it's not chatting. Check if the agent should reflect uppon recent conversations

            reflectOnConversationsAsync();
        }

        actionChoosingLock.lock();

        try {
            if (currentActionStatus.isFinished() && currentAction.shouldBeRemembered()) {
                AgentPastActions pastActions = getState().getMemory().getPastActions();

                pastActions.addMemorySection(new PastAction(currentAction, Instant.now()));

                // Forgetting old actions
                pastActions.forgetMemorySectionOlderThan(Instant.now().minus(10, ChronoUnit.MINUTES));
            }

            if (!currentActionStatus.isFinished()) {
                // Must cancel the ongoing action
                currentAction.cancel(this);
            }

            currentAction = newAction;
            currentActionStatus = ActionStatus.IN_PROGRESS;
        } finally {
            actionChoosingLock.unlock();
        }
    }

    public void reflectOnConversationsSync() {
        if (state.getMemory().getConversations().entrySizes() != 0) {
            Instant now = Instant.now();

            // There's some reflecting to do
            state.requestStateChangeSync(getPromptForConversationReflectingSync());
            // Forgetting the exact conversations
            state.getMemory().getConversations().forgetMemorySectionOlderThan(now);
            state.markDirty();
        }
    }

    public void reflectOnConversationsAsync() {
        MineSocieties.getPlugin().getThreadPool().execute(this::reflectOnConversationsSync);
    }

    public List<LLMMessage> getPromptForConversationReflectingSync() {
        List<LLMMessage> messageList = new ArrayList<>(4);

        // Telling the model exactly what to do
        messageList.add(new LLMMessage(LLMRole.SYSTEM,
                        "You are a knowledge extractor AI. " +
                                "You will receive a person's description and a list of recent conversations inside brackets " +
                                "'<name>: {<description>}{<conversations>}'. " +
                                state.getAdditionalStateFormat() +
                                "\nNote: Both long-term and short-term memories should contain knowledge originated solely from the conversations. " +
                                "If a memory is already present in the person's description, then do not include it. " +
                                "The person's description should serve as context. "
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
                        SHORT_MEMORY_FORMAT_BEGIN + "{Rafael needs to submit chapter 2 tonight before 23h59|Rui Prada said he will help Rafael with chapter 3 in a second}\n" +
                        LONG_MEMORY_FORMAT_BEGIN + "{Rafael is struggling with chapter 3}\n" +
                        "Explanation: Nothing in the conversation suggests a personality change, as such, Rafael's personalities remain the same. " +
                        "Since Rafael was reminded that there's a deadline for tonight, it makes sense for Rafael to no longer be relaxed and instead " +
                        "be stressed. From this conversation, Rafael should remember about the upcoming deadline and Rui Prada's offer for help. Since both " +
                        "of these things will take place shortly, they only need to be remembered for a bit, therefore, they're considered short-term memory. " +
                        "Rafael mentions during the conversation that he's struggling with chapter 3. This should remain in his long-term memory since it's " +
                        "something that can last. No more information can be gathered from the conversation."
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
            builder.append("Option ");
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
        // Getting an AgentLocation instead of a Location since this method may be called outside the main thread
        AgentLocation currentLocation = state.getCurrentLocation();

        // Adding all actions that this agent can take
        possibleActions.add(new SendChatTo());
        possibleActions.add(new Idle()); // Temporary. An agent that chooses to be idle won't do anything until they receive an observation

        // Adding locations that the agent can go to
        for (LocationReference otherLocationReference : state.getAllLocations()) {
            SocialLocation otherLocation = otherLocationReference.getLocation();

            if (otherLocation != null && !otherLocation.isDeleted()) {
                // The location might have been deleted during this iteration process, hence the check
                possibleActions.add(new InformativeGoTo(otherLocation));

                if (otherLocation.hasPossibleActions() && otherLocation.hasAccess(this) && otherLocation.isClose(currentLocation)) {
                    // This location is accessible, close to the NPC, and there are actions that can be executed there
                    possibleActions.addAll(otherLocation.getPossibleActions());
                }
            }
        }

        if (!currentActionStatus.isFinished()) {
            // Agent can decide to continue its current action
            possibleActions.add(new ContinueCurrentAction(currentAction));
        }

        // Removing actions that cannot be executed
        possibleActions.removeIf(action -> !action.canBeExecuted(this));

        return possibleActions;
    }

    @Override
    public void deleted() {

    }

    public void addUuidToContainer(PersistentDataContainer container) {
        addToDataContainer(container, "npc", PersistentDataType.STRING, getUUID().toString());
    }

    @Override
    public void acquiredFishLoot(Collection<ItemStack> fishLoot) {
        addOrDropItems(fishLoot);
    }

    public void addOrDropItems(Collection<ItemStack> items) {
        state.getInventory().addOrDropItem(this, items.toArray(new ItemStack[items.size()]));
        state.markDirty();
    }

    public void deleteAgentsInvalidLocations() {
        AgentMemory memory = state.getMemory();
        SocialLocation home = memory.getHome().getLocation();

        if (home == null) {
            // The agent's home was deleted. They must always have a home, though
            setNewHomeLocation();
        } else {
            // The agent's home is valid. Checking if this agent has access to it
            if (!home.hasAccess(this)) {
                // The User must have removed the agent from the home's access, possibly via editing the json file. Resetting the access
                home.setAccess(new SharedAccess(this));

                MineSocieties.getPlugin().getLocationsManager().saveAsync(home);

                state.markDirty();
            }
        }

        // Checking the other known locations
        AgentKnownLocations knownLocations = memory.getKnownLocations();

        for (LocationReference reference : knownLocations.getMemorySections()) {
            SocialLocation location = reference.getLocation();

            if (location == null || !location.hasAccess(this)) {
                // The location no longer exists or the agent no longer has access to it. Removing it.
                knownLocations.remove(reference);

                state.markDirty();
            }
        }
    }

    public CharacterReference toReference() {
        return new CharacterReference(this);
    }

    // Static methods

    private static NamespacedKey getNamespacedKey(String key) {
        return new NamespacedKey(MineSocieties.getPlugin(), key);
    }

    public static void addUuidToContainer(PersistentDataContainer container, UUID uuid) {
        addToDataContainer(container, "npc", PersistentDataType.STRING, uuid.toString());
    }

    public static @Nullable UUID getUuidFromContainer(PersistentDataContainer container) {
        String uuidString = getFromDataContainer(container, "npc", PersistentDataType.STRING);

        if (uuidString == null) {
            return null;
        } else {
            return UUID.fromString(uuidString);
        }
    }

    public static <T, Z> void addToDataContainer(PersistentDataContainer container, String key, PersistentDataType<T, Z> dataType, Z data) {
        container.set(getNamespacedKey(key), dataType, data);
    }

    public static <T, Z> @Nullable Z getFromDataContainer(PersistentDataContainer container, String key, PersistentDataType<T, Z> dataType) {
        return container.get(getNamespacedKey(key), dataType);
    }

    public static <T, Z> boolean hasInDataContainer(PersistentDataContainer container, String key, PersistentDataType<T, Z> dataType) {
        return container.has(getNamespacedKey(key), dataType);
    }
}
