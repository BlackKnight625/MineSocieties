package ulisboa.tecnico.minesocieties.agents.location;

import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.CharacterReference;
import ulisboa.tecnico.minesocieties.utils.StringUtils;

import java.util.Collection;
import java.util.List;

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
    public abstract boolean isAccessValid();

    /**
     *  Fixes any inconsistencies in the location access. For example, if a SharedAccess references an Agent that
     * was deleted, it must remove it from its access.
     */
    public abstract void fixInconsistencies();

    /**
     * @return
     *  Returns the agents that are strongly connected to the location access. This means that the agent's memory
     * should be updated in case the location gets deleted.
     */
    public abstract Collection<CharacterReference> getStronglyConnectedAgents();

    public abstract Material getGuiMaterial();

    public abstract String getGuiName();

    protected abstract String getGuiDescription();

    public List<String> getGuiDescriptionLines() {
        return StringUtils.splitIntoLines(getGuiDescription(), 30);
    }
}
