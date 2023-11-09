package ulisboa.tecnico.minesocieties.agents.actions;

import ulisboa.tecnico.minesocieties.visitors.IActionExplainerVisitor;

public interface IExplainableAction {

    void accept(IActionExplainerVisitor visitor);
}
