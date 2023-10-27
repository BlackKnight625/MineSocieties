package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

public class AgentShortTermMemory extends TemporaryMemory<ShortTermMemorySection> implements IExplainableContext {
    @Override
    public String accept(IContextVisitor visitor) {
        return visitor.explainShortTermMemory(this);
    }
}
