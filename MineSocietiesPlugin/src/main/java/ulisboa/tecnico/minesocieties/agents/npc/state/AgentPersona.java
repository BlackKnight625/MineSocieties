package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

import java.time.Instant;

public class AgentPersona implements IExplainableContext {

    // Private attributes

    private String name;
    private int age;
    private Instant birthday;

    // Constructors

    public AgentPersona() {}

    public AgentPersona(String name, int age, Instant birthday) {
        this.name = name;
        this.age = age;
        this.birthday = birthday;
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public Instant getBirthday() {
        return birthday;
    }

    // Other methods


    @Override
    public String accept(IContextVisitor visitor) {
        return visitor.explainPersona(this);
    }
}
