package ulisboa.tecnico.minesocieties.agents.npc.state;

import java.time.Instant;

public class Reflection extends InstantMemory {

    // Private attributes

    private final String reflection;

    // Constructors

    public Reflection(Instant instant, String reflection) {
        super(instant);

        this.reflection = reflection;
    }

    // Getters and setters

    public String getReflection() {
        return reflection;
    }
}
