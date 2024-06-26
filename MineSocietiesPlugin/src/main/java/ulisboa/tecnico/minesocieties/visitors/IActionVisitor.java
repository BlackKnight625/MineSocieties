package ulisboa.tecnico.minesocieties.visitors;

import ulisboa.tecnico.minesocieties.agents.actions.jobActions.InformativeFarming;
import ulisboa.tecnico.minesocieties.agents.actions.jobActions.InformativeGoFishing;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.*;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.GiveItemTo;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.SendChatTo;

public interface IActionVisitor {

    String visitGoTo(InformativeGoTo informativeGoTo);

    String visitIdle(Idle idle);

    String visitWaitFor(WaitFor waitFor);

    String visitSendChatTo(SendChatTo sendChatTo);

    String visitContinueCurrentAction(ContinueCurrentAction continueCurrentAction);

    String visitThinking(Thinking thinking);

    String visitGoFishing(InformativeGoFishing informativeGoFishing);

    String visitGiveItemTo(GiveItemTo giveItemTo);

    String visitFarming(InformativeFarming farming);
}
