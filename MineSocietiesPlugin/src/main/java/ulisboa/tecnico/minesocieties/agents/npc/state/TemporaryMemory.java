package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.agents.utils.ReadWriteLock;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class TemporaryMemory<T extends InstantMemory> {

    // Private attributes

    private Collection<T> memory = new HashSet<>();
    private transient ReadWriteLock memoryLock = new ReadWriteLock();

    // Other methods

    public void addMemorySection(T memory) {
        memoryLock.write(() -> this.memory.add(memory));
    }

    public void forgetMemorySectionOlderThan(Instant instant) {
        memoryLock.write(() -> memory.removeIf(memorySection -> memorySection.getInstant().compareTo(instant) < 0));
    }

    public Collection<T> getMemorySectionsSince(Instant instant) {
        memoryLock.readLock();
        var result = memory.stream().filter(memorySection -> memorySection.getInstant().compareTo(instant) > 0).toList();
        memoryLock.readUnlock();

        return result;
    }

    public Collection<T> getMemorySections() {
        memoryLock.readLock();
        var result = new ArrayList<>(memory);
        memoryLock.readUnlock();

        return result;
    }

    public int entrySizes() {
        memoryLock.readLock();
        int size = memory.size();
        memoryLock.readUnlock();

        return size;
    }
}
