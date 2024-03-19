package ulisboa.tecnico.minesocieties.agents.location;

import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.CharacterReference;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SharedAccess extends LocationAccess {

    // Private attributes

    private Set<CharacterReference> agentsWithAccess = new HashSet<>();

    // Constructors

    public SharedAccess() {
        super(LocationAccessType.SHARED);
    }

    public SharedAccess(SocialAgent... agents) {
        super(LocationAccessType.SHARED);

        agentsWithAccess.addAll(Arrays.stream(agents).map(SocialAgent::toReference).toList());
    }

    // Other methods

    @Override
    public Collection<CharacterReference> getCharactersWithAccess(SocialLocation location) {
        return agentsWithAccess;
    }

    @Override
    public boolean hasAccess(SocialAgent agent, SocialLocation location) {
        return agentsWithAccess.contains(new CharacterReference(agent));
    }

    @Override
    public boolean isValid() {
        return !agentsWithAccess.isEmpty(); // If no agent knows about this shared location, then it's invalid
    }

    @Override
    public void fixInconsistencies() {
        agentsWithAccess.removeIf(characterReference -> characterReference.getReferencedCharacter() == null);
    }

    @Override
    public Collection<CharacterReference> getStronglyConnectedAgents() {
        return agentsWithAccess;
    }

    @Override
    public String toString() {
        return "SharedAccess{" +
                "agentsWithAccess=" + agentsWithAccess +
                '}';
    }
}
