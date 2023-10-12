package ulisboa.tecnico.minesocieties.visitors;

import ulisboa.tecnico.minesocieties.agents.actions.exceptions.MalformedActionArgumentsException;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.SendChatTo;

public interface IActionArgumentsExplainerVisitor {

    String explainSendChatToArguments(SendChatTo sendChatTo);

    void setArgumentsOfSendChatTo(SendChatTo sendChatTo, String arguments) throws MalformedActionArgumentsException;
}
