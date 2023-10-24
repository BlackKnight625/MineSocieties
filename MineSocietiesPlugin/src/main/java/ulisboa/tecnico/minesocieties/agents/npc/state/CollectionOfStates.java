package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.agents.utils.ReadWriteLock;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CollectionOfStates<T extends ISimpleExplanation> {

    // Private attributes

    private Set<String> states = new HashSet<>();
    private transient ReadWriteLock statesLock = new ReadWriteLock();

    // Other methods

    public void addState(T state) {
        statesLock.write(() -> states.add(state.getExplanation()));
    }

    public void addState(String customState) {
        statesLock.write(() -> states.add(customState));
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
}
