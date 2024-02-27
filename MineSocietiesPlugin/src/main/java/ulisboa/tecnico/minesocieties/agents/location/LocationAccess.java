package ulisboa.tecnico.minesocieties.agents.location;

import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.CharacterReference;

import java.util.Collection;

public abstract class LocationAccess {

    // Other methods

    /**
     * Returns the characters with access to the location.
     *
     * @param location the location
     * @return the characters with access to the location
     */
    public abstract Collection<CharacterReference> getCharactersWithAccess(SocialLocation location);

    public abstract boolean hasAccess(SocialAgent agent, SocialLocation location);
}
