package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

public class AgentState implements IExplainableContext {

    // Private attributes

    private AgentMemory memory;
    private AgentMoods moods = new AgentMoods();
    private AgentPersonalities personalities = new AgentPersonalities();
    private AgentPersona persona;

    // Constructors

    public AgentState() {}

    public AgentState(AgentPersona persona, AgentLocation home) {
        this.memory = new AgentMemory(home);
        this.persona = persona;
    }

    // Getters and setters

    public AgentMemory getMemory() {
        return memory;
    }

    public AgentMoods getMoods() {
        return moods;
    }

    public AgentPersonalities getPersonalities() {
        return personalities;
    }

    public AgentPersona getPersona() {
        return persona;
    }

    // Other methods


    @Override
    public String accept(IContextVisitor visitor) {
        return visitor.explainState(this);
    }
}
