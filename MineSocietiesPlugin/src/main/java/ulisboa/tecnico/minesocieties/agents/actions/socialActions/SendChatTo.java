package ulisboa.tecnico.minesocieties.agents.actions.socialActions;

import org.bukkit.Bukkit;
import ulisboa.tecnico.agents.actions.ActionStatus;
import ulisboa.tecnico.agents.observation.ReceivedChatObservation;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.SocialCharacter;
import ulisboa.tecnico.minesocieties.agents.actions.IActionWithArguments;
import ulisboa.tecnico.minesocieties.agents.actions.ISocialAction;
import ulisboa.tecnico.minesocieties.agents.actions.exceptions.MalformedActionArgumentsException;
import ulisboa.tecnico.minesocieties.agents.npc.Message;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentReference;
import ulisboa.tecnico.minesocieties.utils.ComponentUtils;
import ulisboa.tecnico.minesocieties.utils.LocationUtils;
import ulisboa.tecnico.minesocieties.visitors.IActionArgumentsExplainerVisitor;
import ulisboa.tecnico.minesocieties.visitors.IActionVisitor;

import java.util.LinkedList;
import java.util.List;

public class SendChatTo implements IActionWithArguments, ISocialAction {

    // Private attributes

    private String message;
    private AgentReference receiver;

    // Getters and setters

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AgentReference getReceiver() {
        return receiver;
    }

    public void setReceiver(AgentReference receiver) {
        this.receiver = receiver;
    }

    // Other methods

    public List<String> getNamesOfNearbyCharacters(SocialAgent agent) {
        return LocationUtils.getNearbyCharacterNamesExcludingSelf(agent.getLocation(), agent.getUUID());
    }

    @Override
    public void acceptArgumentsInterpreter(IActionArgumentsExplainerVisitor visitor, String arguments) throws MalformedActionArgumentsException {
        visitor.setArgumentsOfSendChatTo(this, arguments);
    }

    @Override
    public String acceptArgumentsExplainer(IActionArgumentsExplainerVisitor visitor, SocialAgent actioner) {
        return visitor.explainSendChatToArguments(this, actioner);
    }

    @Override
    public ActionStatus act(SocialAgent actioner) {
        // TODO: Also show player's messages in a similar format in chat. Distinguish between the options (BROADCAST_ALL, BROADCAST_NEARBY, PRIVATE),
        // where if server owners choose BROADCAST_ALL, all messages between agents <-> players and agents <-> agents are broadcasted to
        // everyone, BROADCAST_NEARBY only broadcasts messages between agents <-> players and agents <-> agents to nearby players, 
        // and PRIVATE only shows messages to the ones engaging in conversation
        SocialCharacter characterReceiver = MineSocieties.getPlugin().getSocialAgentManager().getCharacter(receiver.getUuid());

        ReceivedChatObservation observation = new ReceivedChatObservation(actioner, message);

        characterReceiver.receivedChatFrom(observation);

        actioner.getAgent().lookAt(characterReceiver.getLocation());

        int messageDurationTicks = Math.max(20, message.length() * 2); // More or less 0.5 seconds per word. At least 1 second total

        actioner.getMessageDisplay().displayMessage(new Message(messageDurationTicks,
                ComponentUtils.speechBubbleTo(receiver.getName(), message)));

        if (MineSocieties.getPlugin().isChatBroadcasted()) {
            Bukkit.broadcast(ComponentUtils.sendMessageToPrefix(actioner.getName(), receiver.getName(), message));
        }

        return ActionStatus.SUCCESS;
    }

    @Override
    public boolean canBeExecuted(SocialAgent actioner) {
        // Agent can send a chat as long as there are people to chat with
        return !getNamesOfNearbyCharacters(actioner).isEmpty();
    }

    @Override
    public String accept(IActionVisitor visitor) {
        return visitor.visitSendChatTo(this);
    }

    @Override
    public int getThinkingTicks() {
        return 60;
    }

    @Override
    public boolean shouldBeRemembered() {
        return false; // Conversations have their own section in an agent's memory
    }
}
