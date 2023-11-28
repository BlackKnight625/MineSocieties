package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public class AgentPersona implements IExplainableContext {

    // Private attributes

    private String name;
    private Instant birthday;

    // Constructors

    public AgentPersona() {}

    public AgentPersona(String name, Instant birthday) {
        this.name = name;
        this.birthday = birthday;
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public int getAge() {
        return Period.between(LocalDate.ofInstant(birthday, ZoneId.systemDefault()), LocalDate.now()).getYears();
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
