package ulisboa.tecnico.minesocieties.agents.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.actions.ISocialAction;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentLocation;
import ulisboa.tecnico.minesocieties.agents.npc.state.CharacterReference;
import ulisboa.tecnico.minesocieties.agents.npc.state.IExplainableContext;
import ulisboa.tecnico.minesocieties.agents.npc.state.ISimpleExplanation;
import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

import java.util.*;

/**
 *  Represents a location that agents can know about.
 *  A location can be known to certain agents.
 *  Certain actions can only be executed near certain locations.
 */
public class SocialLocation implements IExplainableContext, ISimpleExplanation {

    // Private attributes

    private UUID uuid;
    private Vector position;
    private String worldName;
    private String name;
    private LocationAccess access;
    private Set<LocationBoundActionType> possibleActionTypes = new LinkedHashSet<>();
    private Material guiMaterial;
    private transient boolean deleted = false;

    // Constructors

    public SocialLocation(Vector position, String worldName, String name, LocationAccess access) {
        this.position = position;
        this.worldName = worldName;
        this.name = name;

        this.uuid = UUID.randomUUID();
        this.guiMaterial = Material.RECOVERY_COMPASS;

        setAccess(access);
    }

    public SocialLocation(Location location, String name, LocationAccess access) {
        this(location.toVector(), location.getWorld().getName(), name, access);
    }

    public SocialLocation() {}

    // Getters and setters

    public Vector getPosition() {
        return position;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocationAccess getAccess() {
        return access;
    }

    public void setAccess(LocationAccess access) {
        if (this.access != null) {
            this.access.deleted(this);
        }

        this.access = access;

        access.initialize(this);
    }

    public UUID getUuid() {
        return uuid;
    }

    public Collection<LocationBoundActionType> getPossibleActionTypes() {
        return possibleActionTypes;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Material getGuiMaterial() {
        return guiMaterial;
    }

    public void setGuiMaterial(Material guiMaterial) {
        this.guiMaterial = guiMaterial;
    }

    // Other methods

    /**
     *  Checks if this location is a special location for this Agent, meaning it has special treatment.
     *  For example, the agent's home is stored in a special place in their Memory.
     * @param agent
     *  The agent whose special locations are being checked.
     * @return
     *  True if this location is a special location for the agent.
     */
    public boolean isSpecialLocation(SocialAgent agent) {
        return agent.getState().getMemory().getHome().equals(toReference());
    }

    public boolean isSpecialLocation(CharacterReference agent) {
        return isSpecialLocation((SocialAgent) agent.getReferencedCharacter());
    }

    public void addPossibleAction(LocationBoundActionType actionType) {
        possibleActionTypes.add(actionType);
    }

    public void removePossibleAction(LocationBoundActionType actionType) {
        possibleActionTypes.remove(actionType);
    }

    public boolean hasPossibleAction(LocationBoundActionType actionType) {
        return possibleActionTypes.contains(actionType);
    }

    public Location toBukkitLocation() throws NullPointerException {
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            throw new RuntimeException("Tried to convert " + this + " to a Bukkit Location, and the world '" + worldName + "' does " +
                    "not exist!");
        }

        return position.toLocation(world);
    }

    @Override
    public String accept(IContextVisitor visitor) {
        return visitor.explainLocation(this);
    }

    @Override
    public String getExplanation() {
        return name;
    }

    public boolean isAccessValid() {
        return access.isAccessValid();
    }

    public void fixInconsistencies() {
        access.fixInconsistencies();
    }

    public LocationReference toReference() {
        return new LocationReference(this);
    }

    public boolean hasAccess(SocialAgent agent) {
        return access.hasAccess(agent, this);
    }

    public Collection<CharacterReference> getAgentsWithAccessCopy() {
        return new ArrayList<>(access.getAgentsWithAccess());
    }

    public void deleted() {
        deleted = true;
    }

    public boolean isClose(Location location) {
        return location.getWorld().getName().equals(worldName) &&
                location.toVector().distanceSquared(position) <= MineSocieties.getPlugin().getLocationBoundActionRangeSquared();
    }

    public boolean isClose(AgentLocation location) {
        return location.getWorldName().equals(worldName) &&
                location.getPosition().distanceSquared(position) <= MineSocieties.getPlugin().getLocationBoundActionRangeSquared();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SocialLocation that = (SocialLocation) o;

        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "SocialLocation{" +
                "uuid=" + uuid +
                ", position=" + position +
                ", worldName='" + worldName + '\'' +
                ", name='" + name + '\'' +
                ", access=" + access +
                '}';
    }

    /**
     * @return
     * A list of possible actions that can be executed at this location.
     */
    public List<ISocialAction> getPossibleActions() {
        List<ISocialAction> actions = new ArrayList<>(possibleActionTypes.size());

        for (LocationBoundActionType actionType : possibleActionTypes) {
            actions.add(actionType.toNewSocialAction());
        }

        return actions;
    }

    public boolean hasPossibleActions() {
        return !possibleActionTypes.isEmpty();
    }
}
