package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

public class AgentLongTermMemory extends TemporaryMemory<LongTermMemorySection> implements IExplainableContext {
    @Override
    public String accept(IContextVisitor visitor) {
        return visitor.explainLongTermMemory(this);
    }
}
