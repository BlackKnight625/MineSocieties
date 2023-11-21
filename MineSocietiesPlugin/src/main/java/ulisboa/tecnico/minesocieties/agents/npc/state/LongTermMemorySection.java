package ulisboa.tecnico.minesocieties.agents.npc.state;

import java.time.Instant;

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
}
