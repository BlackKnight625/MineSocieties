package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.minesocieties.agents.location.LocationReference;
import ulisboa.tecnico.minesocieties.agents.location.SocialLocation;
import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

public class AgentMemory implements IExplainableContext {

    // Private attributes

    private AgentConversations conversations = new AgentConversations();
    private AgentShortTermMemory shortTermMemory = new AgentShortTermMemory();
    private AgentLongTermMemory longTermMemory = new AgentLongTermMemory();
    private AgentPastActions pastActions = new AgentPastActions();
    private LocationReference home;
    private AgentKnownLocations knownLocations = new AgentKnownLocations();

    // Constructors

    public AgentMemory() {}

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

    public LocationReference getHome() {
        return home;
    }

    public void setHome(LocationReference home) {
        this.home = home;
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
