package ulisboa.tecnico.agents.actions;

import ulisboa.tecnico.agents.actions.otherActions.GoTo;

public interface IActionVisitor {

    void visitGoTo(GoTo goTo);
}
