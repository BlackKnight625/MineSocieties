package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

public class AgentMemory implements IExplainableContext {

    // Private attributes

    private AgentConversations conversations = new AgentConversations();
    // TODO: Create a subclass of memory sections that mentions when it took place when converted to natural language
    private AgentShortTermMemory shortTermMemory = new AgentShortTermMemory();
    private AgentLongTermMemory longTermMemory = new AgentLongTermMemory();
    private AgentLocation home;

    // Constructors

    public AgentMemory() {}

    public AgentMemory(AgentLocation home) {
        this.home = home;
    }

    // Getters and setters

    public AgentConversations getConversations() {
        return conversations;
    }

    public AgentShortTermMemory getShortTermMemory() {
        return shortTermMemory;
    }

    public AgentLongTermMemory getLongTermMemory() {
        return longTermMemory;
    }

    public AgentLocation getHome() {
        return home;
    }

    // Other methods


    @Override
    public String accept(IContextVisitor visitor) {
        return visitor.explainMemory(this);
    }
}
