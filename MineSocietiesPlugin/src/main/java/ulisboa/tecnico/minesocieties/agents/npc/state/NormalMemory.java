package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.agents.utils.ReadWriteLock;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

public class NormalMemory<T> {

    // Protected attributes

    protected Collection<T> memory = new LinkedHashSet<>(); // To keep the order of insertion due to possible user editing
    protected transient ReadWriteLock memoryLock = new ReadWriteLock();

    // Other methods

    public void addMemorySection(T memory) {
        memoryLock.write(() -> {
            if (!this.memory.contains(memory)) {
                this.memory.add(memory);
            }
        });
    }

    public Collection<T> getMemorySections() {
        memoryLock.readLock();
        var result = new ArrayList<>(memory);
        memoryLock.readUnlock();

        return result;
    }

    public void remove(T memorySection) {
        memoryLock.write(() -> memory.remove(memorySection));
    }

    public int entrySizes() {
        memoryLock.readLock();
        int size = memory.size();
        memoryLock.readUnlock();

        return size;
    }

    public void reset() {
        memoryLock.write(() -> memory.clear());
    }
}
