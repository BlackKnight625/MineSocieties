package ulisboa.tecnico.minesocieties.agents.npc.state;

import org.jetbrains.annotations.Nullable;
import ulisboa.tecnico.agents.utils.ReadWriteLock;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.SocialCharacter;
import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

import java.util.*;

/**
 *  Represents the opinions that an agent holds regarding others.
 * Once an opinion is formed about another agent, this agent will always hold an opinion regarding that agent.
 * That opinion may change overtime.
 */
public class AgentOpinions implements IExplainableContext {

    // Private attributes

    private Map<String, Opinion> opinions = new HashMap<>();
    private transient ReadWriteLock opinionsLock = new ReadWriteLock();

    // Other methods

    public void formOpinion(String othersName, Opinion opinion) {
        opinionsLock.write(() -> opinions.put(othersName, opinion));
    }

    public @Nullable Opinion getOpinion(String from) {
        opinionsLock.readLock();
        Opinion opinion = opinions.get(from);
        opinionsLock.readUnlock();

        return opinion;
    }

    public @Nullable Opinion getOpinion(UUID from) {
        SocialCharacter fromCharacter = MineSocieties.getPlugin().getSocialAgentManager().getCharacter(from);

        return getOpinion(fromCharacter.getName());
    }

    public Map<String, Opinion> getAllOpinions() {
        opinionsLock.readLock();
        var result = new HashMap<>(opinions);
        opinionsLock.readUnlock();

        return result;
    }

    @Override
    public String accept(IContextVisitor visitor) {
        return visitor.explainOpinions(this);
    }

    public void reset() {
        opinionsLock.write(() -> opinions.clear());
    }
}
