package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

public class AgentState implements IExplainableContext {

    // Private attributes

    private AgentMemory memory;
    private AgentMoods moods;
    private AgentPersonalities personalities;
    private AgentPersona persona;

    // Constructors

    public AgentState() {}

    public AgentState(AgentMemory memory, AgentPersona persona) {
        this.memory = memory;
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
