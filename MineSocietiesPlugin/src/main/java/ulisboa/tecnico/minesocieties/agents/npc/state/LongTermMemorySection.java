package ulisboa.tecnico.minesocieties.agents.npc.state;

import java.time.Instant;
import java.util.Objects;

public class LongTermMemorySection extends InstantMemory {

    // Private attributes

    private String memorySection;

    // Constructors

    public LongTermMemorySection(Instant instant, String memorySection) {
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
        LongTermMemorySection that = (LongTermMemorySection) o;
        return memorySection.equals(that.memorySection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memorySection);
    }
}
