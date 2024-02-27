package ulisboa.tecnico.minesocieties.agents.location;

import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.CharacterReference;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SharedAccess extends LocationAccess {

    // Private attributes

    private Set<CharacterReference> agentsWithAccess = new HashSet<>();

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
    public String toString() {
        return "SharedAccess{" +
                "agentsWithAccess=" + agentsWithAccess +
                '}';
    }
}
