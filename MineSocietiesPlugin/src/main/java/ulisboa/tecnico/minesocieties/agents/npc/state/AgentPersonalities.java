package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

public class AgentPersonalities extends CollectionOfStates<Personality> implements IExplainableContext {
    @Override
    public String accept(IContextVisitor visitor) {
        return visitor.explainPersonalities(this);
    }
}
