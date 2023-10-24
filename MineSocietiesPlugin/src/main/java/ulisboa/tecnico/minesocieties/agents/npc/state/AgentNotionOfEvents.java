package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

public class AgentNotionOfEvents extends TemporaryMemory<NotionOfEvent> implements IExplainableContext {
    @Override
    public String accept(IContextVisitor visitor) {
        return visitor.explainNotionOfEvents(this);
    }
}
