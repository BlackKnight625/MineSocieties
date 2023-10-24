package ulisboa.tecnico.minesocieties.agents.npc.state;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public class InstantMemory implements Comparable<InstantMemory> {

    // Private attributes

    private final Instant instant;

    // Constructors

    public InstantMemory(Instant instant) {
        this.instant = instant;
    }

    // Getters and setters

    public Instant getInstant() {
        return instant;
    }

    // Other methods

    @Override
    public int compareTo(@NotNull InstantMemory o) {
        return instant.compareTo(o.instant);
    }
}
