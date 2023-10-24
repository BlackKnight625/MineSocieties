package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

/**
 *  Represents conversations that this agent has had
 */
public class AgentConversations extends TemporaryMemory<Conversation> implements IExplainableContext {
    @Override
    public String accept(IContextVisitor visitor) {
        return visitor.explainConversations(this);
    }
}
