package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.agents.utils.ReadWriteLock;

import java.util.*;

public class CollectionOfStates<T extends ISimpleExplanation> {

    // Private attributes

    private Set<String> states = new LinkedHashSet<>(); // To keep the order of insertion due to possible user editing
    private transient ReadWriteLock statesLock = new ReadWriteLock();

    // Other methods

    public void addState(T... state) {
        statesLock.write(() -> states.addAll(Arrays.stream(state).map(ISimpleExplanation::getExplanation).toList()));
    }

    public void addState(String... customState) {
        statesLock.write(() -> states.addAll(Arrays.stream(customState).toList()));
    }

    public void removeState(T state) {
        statesLock.write(() -> states.remove(state.getExplanation()));
    }

    public void removeState(String customState) {
        statesLock.write(() -> states.remove(customState));
    }

    public Collection<String> getStates() {
        statesLock.readLock();
        var result = new HashSet<>(states);
        statesLock.readUnlock();

        return result;
    }

    public void reset() {
        statesLock.write(() -> states.clear());
    }

    public int entrySizes() {
        statesLock.readLock();
        int size = states.size();
        statesLock.readUnlock();

        return size;
    }
}
