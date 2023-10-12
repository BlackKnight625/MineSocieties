package ulisboa.tecnico.minesocieties.visitors;

import ulisboa.tecnico.minesocieties.agents.actions.otherActions.Idle;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.InformativeGoTo;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.WaitFor;

public interface IActionExplainerVisitor {

    String explainGoTo(InformativeGoTo informativeGoTo);

    String explainIdle(Idle idle);

    String explainWaitFor(WaitFor waitFor);
}
