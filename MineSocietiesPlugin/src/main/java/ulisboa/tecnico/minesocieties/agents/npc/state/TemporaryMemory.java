package ulisboa.tecnico.minesocieties.agents.npc.state;

import java.time.Instant;
import java.util.Collection;

public class TemporaryMemory<T extends InstantMemory> extends NormalMemory<T> {

    // Other methods

    public void forgetMemorySectionOlderThan(Instant instant) {
        memoryLock.write(() -> memory.removeIf(memorySection -> memorySection.getInstant().compareTo(instant) < 0));
    }

    public Collection<T> getMemorySectionsSince(Instant instant) {
        memoryLock.readLock();
        var result = memory.stream().filter(memorySection -> memorySection.getInstant().compareTo(instant) > 0).toList();
        memoryLock.readUnlock();

        return result;
    }
}
