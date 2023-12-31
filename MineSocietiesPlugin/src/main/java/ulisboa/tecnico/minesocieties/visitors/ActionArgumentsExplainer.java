package ulisboa.tecnico.minesocieties.visitors;

import org.bukkit.Bukkit;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.SocialCharacter;
import ulisboa.tecnico.minesocieties.agents.actions.exceptions.MalformedActionArgumentsException;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.SendChatTo;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentReference;

import java.util.List;

/**
 *  Explains in Natural Language how ChatGPT should present the arguments should it choose a specific action.
 *  The explanation will follow "If you choose this action, "
 */
public class ActionArgumentsExplainer implements IActionArgumentsExplainerVisitor {

    @Override
    public String explainSendChatToArguments(SendChatTo sendChatTo, SocialAgent actioner) {
        StringBuilder builder = new StringBuilder();

        builder.append("write the name of the person who should receive the message and then the message in this format: name|message. The " +
                "possible people to chat with are {");

        List<String> nearbyPeople;

        // This method is most likely being called asynchronously. Just in case it isn't, the names of the agents try to
        // get fetched synchronously.
        if (Bukkit.isPrimaryThread()) {
            nearbyPeople = sendChatTo.getNamesOfNearbyCharacters(actioner);
        }
        else {
            // Must get the names of nearby agents in the main thread
            try {
                nearbyPeople = MineSocieties.getPlugin().getServer().getScheduler().callSyncMethod(MineSocieties.getPlugin(), () ->
                    sendChatTo.getNamesOfNearbyCharacters(actioner)).get();
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }

        if (nearbyPeople.isEmpty()) {
            // This case should never happen as the isExecutable() method from SendChatTo ensures that there's always
            // people to talk to when this action should be explained.
            return "}";
        } else {
            for (String name : nearbyPeople) {
                builder.append(name).append(", ");
            }

            // Deleting last comma and space
            builder.delete(builder.length() - 2, builder.length());
            builder.append('}');
        }

        return builder.toString();
    }

    @Override
    public void setArgumentsOfSendChatTo(SendChatTo sendChatTo, String arguments) throws MalformedActionArgumentsException {
        int barIndex = arguments.indexOf('|');

        if (barIndex == -1) {
            throw new MalformedActionArgumentsException(arguments, "There's no bar '|' separating the name of the message " +
                    "receiver and the message itself");
        }

        String receiverName = arguments.substring(0, barIndex);
        String message = arguments.substring(barIndex + 1);

        SocialCharacter receiver = MineSocieties.getPlugin().getSocialAgentManager().getCharacter(receiverName);

        if (receiver == null) {
            throw new MalformedActionArgumentsException(arguments, "The specified character that should receive the message " +
                    "does not exist");
        }

        sendChatTo.setReceiver(new AgentReference(receiver));
        sendChatTo.setMessage(message);
    }
}
