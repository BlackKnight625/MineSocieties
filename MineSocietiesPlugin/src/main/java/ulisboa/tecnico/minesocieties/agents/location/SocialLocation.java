package ulisboa.tecnico.minesocieties.agents.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import ulisboa.tecnico.minesocieties.agents.actions.ISocialAction;
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
    private String description;
    private LocationAccess access;
    private Set<LocationBoundActionType> possibleActionTypes = new LinkedHashSet<>();

    // Constructors

    public SocialLocation(Vector position, String worldName, String name, String description, LocationAccess access) {
        this.position = position;
        this.worldName = worldName;
        this.name = name;
        this.description = description;
        this.access = access;

        this.uuid = UUID.randomUUID();
    }

    public SocialLocation(Location location, String name, String description, LocationAccess access) {
        this(location.toVector(), location.getWorld().getName(), name, description, access);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocationAccess getAccess() {
        return access;
    }

    public void setAccess(LocationAccess access) {
        this.access = access;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Collection<LocationBoundActionType> getPossibleActionTypes() {
        return possibleActionTypes;
    }

    // Other methods

    public void addPossibleAction(LocationBoundActionType actionType) {
        possibleActionTypes.add(actionType);
    }

    public void removePossibleAction(LocationBoundActionType actionType) {
        possibleActionTypes.remove(actionType);
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
        return description;
    }

    public boolean isValid() {
        return access.isValid();
    }

    public void fixInconsistencies() {
        access.fixInconsistencies();
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
                ", description='" + description + '\'' +
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
}
