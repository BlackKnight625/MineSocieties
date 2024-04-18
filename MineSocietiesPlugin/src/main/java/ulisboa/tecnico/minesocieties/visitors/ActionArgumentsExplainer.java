package ulisboa.tecnico.minesocieties.visitors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.SocialCharacter;
import ulisboa.tecnico.minesocieties.agents.actions.exceptions.MalformedActionArgumentsException;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.GiveItemTo;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.INearbyInteraction;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.SendChatTo;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.CharacterReference;

import java.util.List;

/**
 *  Explains in Natural Language how ChatGPT should present the arguments should it choose a specific action.
 *  The explanation will follow "If you choose this action, "
 */
public class ActionArgumentsExplainer implements IActionArgumentsExplainerVisitor {

    @Override
    public String explainSendChatToArguments(SendChatTo sendChatTo, SocialAgent actioner) {
        StringBuilder builder = new StringBuilder();

        builder.append("write the name of the person who should receive the message, then the message and then " +
                "whether it makes sense for " + actioner.getName() + " to wait for a reply (a 'yes' or 'no') in this format: name|message|wait_for_reply. The " +
                "possible people to chat with are {");

        addNearbyPeople(actioner, sendChatTo, builder);

        return builder.toString();
    }

    @Override
    public void setArgumentsOfSendChatTo(SendChatTo sendChatTo, String arguments) throws MalformedActionArgumentsException {
        String[] split = arguments.split("\\|");

        if (split.length == 1) {
            throw new MalformedActionArgumentsException(arguments, "There's no bar '|' separating the name of the message " +
                    "receiver, the message itself and the boolean");
        }

        if (split.length == 2) {
            throw new MalformedActionArgumentsException(arguments, "There's no bar '|' separating the message and the boolean");
        }

        String receiverName = split[0];
        String message = split[1];
        String waitForReply = split[2];

        if (!waitForReply.equals("yes") && !waitForReply.equals("no")) {
            throw new MalformedActionArgumentsException(arguments, "The 'wait_for_reply' argument should be either 'yes' or 'no'. The LLM " +
                    "wrote " + waitForReply);
        }

        SocialCharacter receiver = MineSocieties.getPlugin().getSocialAgentManager().getCharacter(receiverName);

        if (receiver == null) {
            throw new MalformedActionArgumentsException(arguments, "The specified character that should receive the message " +
                    "does not exist");
        }

        sendChatTo.setReceiver(new CharacterReference(receiver));
        sendChatTo.setMessage(message);
        sendChatTo.setWaitForReply(waitForReply.equals("yes"));
    }

    @Override
    public String explainGiveItemTo(GiveItemTo giveItemTo, SocialAgent actioner) {
        StringBuilder builder = new StringBuilder();

        builder.append("write the name of the person who should receive the item, then the item name and then " +
                "the amount of items to give in this format: name|item_name|amount. The " +
                "possible people to give the item to are {");

        addNearbyPeople(actioner, giveItemTo, builder);

        return builder.toString();
    }

    @Override
    public void setArgumentsOfGiveItemTo(GiveItemTo giveItemTo, String arguments) throws MalformedActionArgumentsException {
        String[] split = arguments.split("\\|");

        if (split.length == 1) {
            throw new MalformedActionArgumentsException(arguments, "There's no bar '|' separating the name of the item receiver, " +
                    "the item name and the amount of items to give");
        }

        if (split.length == 2) {
            throw new MalformedActionArgumentsException(arguments, "There's no bar '|' separating the item name and the amount of items to give");
        }

        String receiverName = split[0];
        String itemName = split[1];
        String amount = split[2];

        SocialCharacter receiver = MineSocieties.getPlugin().getSocialAgentManager().getCharacter(receiverName);

        if (receiver == null) {
            throw new MalformedActionArgumentsException(arguments, "The specified character that should receive the item " +
                    "does not exist");
        }

        // Attempting to create an ItemStack

        try {
            ItemStack item = new ItemStack(Material.valueOf(itemName.toUpperCase().replace(' ', '_')), Integer.parseInt(amount));

            giveItemTo.setReceiver(new CharacterReference(receiver));
            giveItemTo.setItem(item);
        } catch (NumberFormatException exception) {
            throw new MalformedActionArgumentsException(arguments, "The amount of items to give is not a number. The LLM wrote " + amount);
        } catch (IllegalArgumentException exception) {
            throw new MalformedActionArgumentsException(arguments, "The item name is not valid. The LLM wrote " + itemName);
        }
    }

    private void addNearbyPeople(SocialAgent actioner, INearbyInteraction action, StringBuilder builder) {
        List<String> nearbyPeople;

        // This method is most likely being called asynchronously. Just in case it isn't, the names of the agents try to
        // get fetched synchronously.

        if (Bukkit.isPrimaryThread()) {
            nearbyPeople = action.getNamesOfNearbyCharacters(actioner);
        }
        else {
            // Must get the names of nearby agents in the main thread
            try {
                nearbyPeople = MineSocieties.getPlugin().getServer().getScheduler().callSyncMethod(MineSocieties.getPlugin(), () ->
                    action.getNamesOfNearbyCharacters(actioner)).get();
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }

        if (nearbyPeople.isEmpty()) {
            // This case should never happen as the isExecutable() method from subclasses of INearbyInteraction
            // should ensure that there's always people to talk to when this action should be explained.
            builder.append("}");
        } else {
            for (String name : nearbyPeople) {
                builder.append(name).append(", ");
            }

            // Deleting last comma and space
            builder.delete(builder.length() - 2, builder.length());
            builder.append('}');
        }
    }
}
