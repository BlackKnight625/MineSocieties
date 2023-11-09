package ulisboa.tecnico.minesocieties.agents.actions.socialActions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import ulisboa.tecnico.agents.actions.ActionStatus;
import ulisboa.tecnico.agents.actions.IAction;
import ulisboa.tecnico.agents.observation.ReceivedChatObservation;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.SocialCharacter;
import ulisboa.tecnico.minesocieties.agents.actions.IActionWithArguments;
import ulisboa.tecnico.minesocieties.agents.actions.IExplainableAction;
import ulisboa.tecnico.minesocieties.agents.actions.exceptions.MalformedActionArgumentsException;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentReference;
import ulisboa.tecnico.minesocieties.visitors.IActionArgumentsExplainerVisitor;
import ulisboa.tecnico.minesocieties.visitors.IActionExplainerVisitor;

import java.util.LinkedList;
import java.util.List;

public class SendChatTo implements IAction<SocialAgent, IActionExplainerVisitor>, IExplainableAction, IActionWithArguments {

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
        List<String> names = new LinkedList<>();
        Player playerAgent = Bukkit.getPlayer(agent.getAgent().getData().getUUID());
        int maxChatRange = MineSocieties.getPlugin().getMaxChatRange();

        for (Entity entity : playerAgent.getWorld().getNearbyEntities(playerAgent.getLocation(), maxChatRange, maxChatRange, maxChatRange)) {
            if (entity instanceof Player other && !playerAgent.getUniqueId().equals(other.getUniqueId())) {
                // Found a nearby player
                names.add(other.getName());
            }
        }

        return names;
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
        SocialCharacter characterReceiver = MineSocieties.getPlugin().getSocialAgentManager().getCharacter(receiver.getUuid());

        ReceivedChatObservation observation = new ReceivedChatObservation(actioner, message);

        characterReceiver.receivedChatFrom(observation);

        // TODO Possible broadcast messages. Display message above agent's head. Make agent look at the message receiver

        return ActionStatus.SUCCESS;
    }

    @Override
    public boolean canBeExecuted(SocialAgent actioner) {
        // Agent can send a chat as long as there are people to chat with
        return !getNamesOfNearbyCharacters(actioner).isEmpty();
    }

    @Override
    public void accept(IActionExplainerVisitor visitor) {
        visitor.visitSendChatTo(this);
    }
}
