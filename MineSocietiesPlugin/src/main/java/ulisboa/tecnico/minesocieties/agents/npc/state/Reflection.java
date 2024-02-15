package ulisboa.tecnico.minesocieties.agents.npc.state;

import java.time.Instant;
import java.util.Objects;

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

    // Other methods

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reflection that = (Reflection) o;
        return reflection.equals(that.reflection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reflection);
    }
}
