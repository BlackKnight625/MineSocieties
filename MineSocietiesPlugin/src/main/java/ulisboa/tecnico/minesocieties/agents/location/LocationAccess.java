package ulisboa.tecnico.minesocieties.agents.location;

import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.CharacterReference;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.social.locations.AgentSelectionMenu;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public abstract class LocationAccess {

    // Private attributes

    private final LocationAccessType type;

    // Constructors

    public LocationAccess(LocationAccessType type) {
        this.type = type;
    }

    // Initialization

    /**
     *  Called when this LocationAccess is set as the given SocialLocation's access.
     *  This is used to let the agents with starting access to this location
     * to memorize it.
     */
    public void initialize(SocialLocation location) {
        LocationReference reference = location.toReference();

        forEachAgentWithAccess(agent -> {
            rememberLocation(reference, agent);
        });
    }

    // Getters and setters

    public LocationAccessType getType() {
        return type;
    }

    // Other methods

    /**
     * Returns the characters with access to the location.
     *
     * @return the characters with access to the location
     */
    public abstract Collection<CharacterReference> getAgentsWithAccess();

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
     * @return
     *  True if any inconsistencies were fixed.
     */
    public abstract boolean fixInconsistencies();

    public Material getGuiMaterial() {
        return type.getGuiMaterial();
    }

    public String getGuiName() {
        return type.getGuiName();
    }

    public List<String> getGuiDescriptionLines() {
        return type.getGuiDescription();
    }

    public AgentSelectionMenu getAccessAgentsEditor(SocialPlayer player, SocialLocation location) {
        return type.getAccessAgentsEditor(player, location, this);
    }

    /**
     *  Called when a location stops having this access. All agents with access must forget about the location
     */
    public void deleted(SocialLocation location) {
        LocationReference reference = location.toReference();

        forEachAgentWithAccess(agent -> {
            forgetLocation(reference, agent);
        });
    }

    public void forgetLocation(LocationReference location, SocialAgent agent) {
        if (!location.getLocation().isSpecialLocation(agent)) {
            agent.getState().getMemory().getKnownLocations().remove(location);
            agent.getState().markDirty();
        }
    }

    public void rememberLocation(LocationReference location, SocialAgent agent) {
        if (!location.getLocation().isSpecialLocation(agent)) {
            agent.getState().getMemory().getKnownLocations().addMemorySection(location);
            agent.getState().markDirty();
        }
    }

    public void forEachAgentWithAccess(Consumer<SocialAgent> action) {
        getAgentsWithAccess().stream().map(agentReference -> (SocialAgent) agentReference.getReferencedCharacter())
                .forEach(action);
    }

    /**
     * @return
     *  Returns true if the access to the location is restricted. This means that not every agent can know about it
     */
    public boolean isRestricted() {
        return true;
    }
}
