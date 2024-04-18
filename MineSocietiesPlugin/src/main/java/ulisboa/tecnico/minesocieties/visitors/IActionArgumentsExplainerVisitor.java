package ulisboa.tecnico.minesocieties.visitors;

import ulisboa.tecnico.minesocieties.agents.actions.exceptions.MalformedActionArgumentsException;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.GiveItemTo;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.SendChatTo;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;

public interface IActionArgumentsExplainerVisitor {

    String explainSendChatToArguments(SendChatTo sendChatTo, SocialAgent actioner);

    void setArgumentsOfSendChatTo(SendChatTo sendChatTo, String arguments) throws MalformedActionArgumentsException;

    String explainGiveItemTo(GiveItemTo giveItemTo, SocialAgent actioner);

    void setArgumentsOfGiveItemTo(GiveItemTo giveItemTo, String arguments) throws MalformedActionArgumentsException;
}
