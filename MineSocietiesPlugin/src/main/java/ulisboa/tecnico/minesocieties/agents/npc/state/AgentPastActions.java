package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

public class AgentPastActions extends TemporaryMemory<PastAction> implements IExplainableContext {
    @Override
    public String accept(IContextVisitor visitor) {
        return visitor.explainPastActions(this);
    }
}
