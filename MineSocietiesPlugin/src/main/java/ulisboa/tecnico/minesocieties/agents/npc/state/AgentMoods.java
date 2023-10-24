package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

public class AgentMoods extends CollectionOfStates<Mood> implements IExplainableContext {
    @Override
    public String accept(IContextVisitor visitor) {
        return visitor.explainMoods(this);
    }
}
