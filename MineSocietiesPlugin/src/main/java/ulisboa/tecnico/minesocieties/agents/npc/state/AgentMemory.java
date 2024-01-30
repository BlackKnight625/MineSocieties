package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

public class AgentMemory implements IExplainableContext {

    // Private attributes

    private AgentConversations conversations = new AgentConversations();
    private AgentShortTermMemory shortTermMemory = new AgentShortTermMemory();
    private AgentLongTermMemory longTermMemory = new AgentLongTermMemory();
    private AgentPastActions pastActions = new AgentPastActions();
    private AgentLocation home;
    private AgentKnownLocations knownLocations = new AgentKnownLocations();

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

    public AgentPastActions getPastActions() {
        return pastActions;
    }

    public AgentLocation getHome() {
        return home;
    }

    public AgentKnownLocations getKnownLocations() {
        return knownLocations;
    }

    // Other methods


    @Override
    public String accept(IContextVisitor visitor) {
        return visitor.explainMemory(this);
    }
}
