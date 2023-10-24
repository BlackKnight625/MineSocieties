package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

public class AgentReflections extends TemporaryMemory<Reflection> implements IExplainableContext {
    @Override
    public String accept(IContextVisitor visitor) {
        return visitor.explainReflections(this);
    }
}
