package ulisboa.tecnico.minesocieties.agents.actions.otherActions;

import org.bukkit.Location;
import ulisboa.tecnico.agents.actions.otherActions.GoTo;
import ulisboa.tecnico.minesocieties.agents.actions.ISocialAction;
import ulisboa.tecnico.minesocieties.agents.location.SocialLocation;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.visitors.IActionVisitor;

public class InformativeGoTo extends GoTo<SocialAgent> implements ISocialAction {

    // Private attributes

    private final String destinationDescription;

    // Constructors

    public InformativeGoTo(Location destination, String destinationDescription) {
        super(destination);

        this.destinationDescription = destinationDescription;
    }

    public InformativeGoTo(SocialLocation socialLocation) {
        super(socialLocation.toBukkitLocation());

        this.destinationDescription = socialLocation.getExplanation();
    }

    // Getters and setters

    public String getDestinationDescription() {
        return destinationDescription;
    }

    // Other methods


    @Override
    public boolean canBeExecuted(SocialAgent actioner) {
        // For now, prevent NPCs from traveling to locations in different worlds
        return actioner.getLocation().getWorld().equals(getDestination().getWorld()) &&
                !destinationIsCloseTo(actioner.getLocation(), 2); // Preventing agents from constantly choosing to go to the location they're already at
    }

    public boolean destinationIsCloseTo(Location location, double distanceThreshold) {
        if (getDestination().getWorld().equals(location.getWorld())) {
            return getDestination().distanceSquared(location) <= distanceThreshold * distanceThreshold;
        } else {
            return false;
        }
    }

    @Override
    public String accept(IActionVisitor visitor) {
        return visitor.visitGoTo(this);
    }
}
