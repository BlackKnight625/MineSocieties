package ulisboa.tecnico.minesocieties.agents.actions.otherActions;

import org.bukkit.Location;
import ulisboa.tecnico.agents.actions.otherActions.GoTo;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.actions.ISocialAction;
import ulisboa.tecnico.minesocieties.agents.location.SocialLocation;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.visitors.IActionVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InformativeGoTo extends GoTo<SocialAgent> implements ISocialAction {

    // Private attributes

    private final SocialLocation socialLocation;

    private static final Random RANDOM = new Random();

    // Constructors

    public InformativeGoTo(SocialLocation socialLocation, boolean goToExactLocation) {
        super(goToExactLocation ? socialLocation.toBukkitLocation() : getRandomCloseValidLocation(socialLocation));

        this.socialLocation = socialLocation;
    }

    // Getters and setters

    public String getDestinationDescription() {
        return socialLocation.getExplanation();
    }

    public SocialLocation getSocialLocation() {
        return this.socialLocation;
    }

    // Other methods

    private static Location getRandomCloseValidLocation(SocialLocation socialLocation) {
        List<Location> candidates = new ArrayList<>();
        List<SocialAgent> validAgents = MineSocieties.getPlugin().getSocialAgentManager().getValidAgents();
        Location bukkitSocialLocation = socialLocation.toBukkitLocation();

        for (int x = -2; x <= 2; x++) {
            for (int y = -1; y <= 1; y++) {
                coordinateLoop: for (int z = -2; z <= 2; z++) {
                    Location candidate = bukkitSocialLocation.clone().add(x, y, z);

                    if (candidate.getBlock().isEmpty() &&
                            candidate.getBlock().getRelative(0, -1, 0).getType().isSolid() &&
                            candidate.getBlock().getRelative(0, 1, 0).isEmpty()) {
                        // Found a valid location. Will also check if there isn't an agent standing on it already

                        for (SocialAgent agent : validAgents) {
                            if (agent.getLocation().distanceSquared(candidate) <= 1) {
                                // Agent is standing on the candidate location
                                continue coordinateLoop;
                            }
                        }

                        candidates.add(candidate);
                    }
                }
            }
        }

        return candidates.isEmpty() ? socialLocation.toBukkitLocation() : candidates.get(RANDOM.nextInt(candidates.size()));
    }


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
