package ulisboa.tecnico.minesocieties.agents.location;

import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.CharacterReference;

import java.util.Collection;

public abstract class LocationAccess {

    // Private attributes

    private final LocationAccessType type;

    // Constructors

    public LocationAccess(LocationAccessType type) {
        this.type = type;
    }

    // Other methods

    /**
     * Returns the characters with access to the location.
     *
     * @param location the location
     * @return the characters with access to the location
     */
    public abstract Collection<CharacterReference> getCharactersWithAccess(SocialLocation location);

    public abstract boolean hasAccess(SocialAgent agent, SocialLocation location);

    /**
     * @return
     *  Returns true if this location access is valid. This being, if the location access's restrictions can be applied.
     *  For example, if a PersonalAccess belongs to an agent that no longer exists, then it doesn't make sense for
     * the restriction brought by the Personal Access to exist. Therefore, the location should be deleted.
     */
    public abstract boolean isValid();

    /**
     *  Fixes any inconsistencies in the location access. For example, if a SharedAccess references an Agent that
     * was deleted, it must remove it from its access.
     */
    public abstract void fixInconsistencies();
}
