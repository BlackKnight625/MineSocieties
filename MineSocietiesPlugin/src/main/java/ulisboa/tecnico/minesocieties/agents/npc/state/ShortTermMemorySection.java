package ulisboa.tecnico.minesocieties.agents.npc.state;

import java.time.Instant;
import java.util.Objects;

public class ShortTermMemorySection extends InstantMemory {

    // Private attributes

    private final String memorySection;

    // Constructors

    public ShortTermMemorySection(Instant instant, String memorySection) {
        super(instant);

        this.memorySection = memorySection;
    }

    // Getters and setters

    public String getMemorySection() {
        return memorySection;
    }

    // Other methods

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShortTermMemorySection that = (ShortTermMemorySection) o;
        return memorySection.equals(that.memorySection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memorySection);
    }
}
