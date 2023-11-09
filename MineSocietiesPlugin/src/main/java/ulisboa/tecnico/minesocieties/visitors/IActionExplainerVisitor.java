package ulisboa.tecnico.minesocieties.visitors;

import ulisboa.tecnico.agents.actions.IActionVisitor;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.Idle;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.InformativeGoTo;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.WaitFor;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.SendChatTo;

public interface IActionExplainerVisitor extends IActionVisitor {

    void visitGoTo(InformativeGoTo informativeGoTo);

    void visitIdle(Idle idle);

    void visitWaitFor(WaitFor waitFor);

    void visitSendChatTo(SendChatTo sendChatTo);
}
