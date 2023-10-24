package ulisboa.tecnico.minesocieties.agents.npc.state;

import org.jetbrains.annotations.Nullable;
import ulisboa.tecnico.agents.utils.ReadWriteLock;
import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

import java.util.*;

/**
 *  Represents the opinions that an agent holds regarding others.
 * Once an opinion is formed about another agent, this agent will always hold an opinion regarding that agent.
 * That opinion may change overtime.
 */
public class AgentOpinions implements IExplainableContext {

    // Private attributes

    private Map<UUID, Opinion> opinions = new HashMap<>();
    private transient ReadWriteLock opinionsLock = new ReadWriteLock();

    // Other methods

    public void formOpinion(Opinion opinion) {
        opinionsLock.write(() -> opinions.put(opinion.getOthersUuid(), opinion));
    }

    public @Nullable Opinion getOpinion(UUID from) {
        opinionsLock.readLock();
        Opinion opinion = opinions.get(from);
        opinionsLock.readUnlock();

        return opinion;
    }

    public Collection<Opinion> getAllOpinions() {
        opinionsLock.readLock();
        var result = new ArrayList<>(opinions.values());
        opinionsLock.readUnlock();

        return result;
    }

    @Override
    public String accept(IContextVisitor visitor) {
        return visitor.explainOpinions(this);
    }
}
