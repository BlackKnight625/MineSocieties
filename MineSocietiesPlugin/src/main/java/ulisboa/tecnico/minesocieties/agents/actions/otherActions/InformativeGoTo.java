package ulisboa.tecnico.minesocieties.agents.actions.otherActions;

import org.bukkit.Location;
import ulisboa.tecnico.agents.actions.otherActions.GoTo;
import ulisboa.tecnico.minesocieties.agents.actions.ISocialAction;
import ulisboa.tecnico.minesocieties.agents.location.SocialLocation;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.visitors.IActionVisitor;

import java.util.List;

public class InformativeGoTo extends GoTo<SocialAgent> implements ISocialAction {

    // Private attributes

    private final SocialLocation socialLocation;

    // Constructors

    public InformativeGoTo(SocialLocation socialLocation) {
        super(socialLocation.toBukkitLocation());

        this.socialLocation = socialLocation;
    }

    // Getters and setters

    public String getDestinationDescription() {
        return socialLocation.getExplanation();
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

    public List<ISocialAction> getPossibleActionsAtDestination() {
        return socialLocation.getPossibleActions();
    }
}
