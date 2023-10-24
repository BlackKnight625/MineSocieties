package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

public class AgentMemory implements IExplainableContext {

    // Private attributes

    private AgentConversations conversations = new AgentConversations();
    private AgentReflections reflections = new AgentReflections();
    private AgentOpinions opinions = new AgentOpinions();
    private AgentNotionOfEvents notionOfEvents = new AgentNotionOfEvents();
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

    public AgentReflections getReflections() {
        return reflections;
    }

    public AgentOpinions getOpinions() {
        return opinions;
    }

    public AgentNotionOfEvents getNotionOfEvents() {
        return notionOfEvents;
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
