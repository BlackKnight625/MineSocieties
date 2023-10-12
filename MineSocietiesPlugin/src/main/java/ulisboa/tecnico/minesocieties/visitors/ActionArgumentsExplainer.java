package ulisboa.tecnico.minesocieties.visitors;

import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.SocialCharacter;
import ulisboa.tecnico.minesocieties.agents.actions.exceptions.MalformedActionArgumentsException;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.SendChatTo;

/**
 *  Explains in Natural Language how ChatGPT should present the arguments should it choose a specific action.
 *  The explanation will follow "If you choose this action, "
 */
public class ActionArgumentsExplainer implements IActionArgumentsExplainerVisitor {

    @Override
    public String explainSendChatToArguments(SendChatTo sendChatTo) {
        return "write the name of the person who should receive the message and then the message in this format: name|message";
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

        sendChatTo.setReceiver(receiver);
        sendChatTo.setMessage(message);
    }
}
