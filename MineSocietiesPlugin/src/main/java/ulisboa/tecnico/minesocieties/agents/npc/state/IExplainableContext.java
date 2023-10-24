package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

public interface IExplainableContext {

    String accept(IContextVisitor visitor);
}
