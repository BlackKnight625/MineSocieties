package ulisboa.tecnico.minesocieties.agents.npc.state;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

public class AgentLocation implements IExplainableContext {

    // Private attributes

    private Vector position;
    private String worldName;
    private String description;

    // Constructors

    public AgentLocation() {}

    public AgentLocation(Vector position, String worldName, String description) {
        this.position = position;
        this.worldName = worldName;
        this.description = description;
    }

    public AgentLocation(Location location) {
        this.position = location.toVector();
        this.worldName = location.getWorld().getName();
    }

    // Getters and setters

    public Vector getPosition() {
        return position;
    }

    public String getWorldName() {
        return worldName;
    }

    public String getDescription() {
        return description;
    }

    // Other methods

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
    public String toString() {
        return "AgentLocation{" +
                "position=" + position +
                ", worldName='" + worldName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
