package ulisboa.tecnico.minesocieties.agents.npc.state;

import java.time.Instant;

public class ShortTermMemorySection extends InstantMemory {

    // Private attributes

    private String memorySection;

    // Constructors

    public ShortTermMemorySection(Instant instant, String memorySection) {
        super(instant);

        this.memorySection = memorySection;
    }

    // Getters and setters

    public String getMemorySection() {
        return memorySection;
    }
}
