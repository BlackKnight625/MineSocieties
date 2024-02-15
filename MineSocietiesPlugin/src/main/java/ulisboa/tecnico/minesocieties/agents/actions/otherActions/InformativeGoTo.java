package ulisboa.tecnico.minesocieties.agents.actions.otherActions;

import org.bukkit.Location;
import ulisboa.tecnico.agents.actions.otherActions.GoTo;
import ulisboa.tecnico.minesocieties.agents.actions.ISocialAction;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentLocation;
import ulisboa.tecnico.minesocieties.visitors.IActionVisitor;

public class InformativeGoTo extends GoTo<SocialAgent> implements ISocialAction {

    // Private attributes

    private final String destinationDescription;

    // Constructors

    public InformativeGoTo(Location destination, String destinationDescription) {
        super(destination);

        this.destinationDescription = destinationDescription;
    }

    public InformativeGoTo(AgentLocation agentLocation) {
        super(agentLocation.toBukkitLocation());

        this.destinationDescription = agentLocation.getDescription();
    }

    // Getters and setters

    public String getDestinationDescription() {
        return destinationDescription;
    }

    // Other methods


    @Override
    public boolean canBeExecuted(SocialAgent actioner) {
        // For now, prevent NPCs from traveling to locations in different worlds
        return actioner.getLocation().getWorld().equals(getDestination().getWorld());
    }

    @Override
    public String accept(IActionVisitor visitor) {
        return visitor.visitGoTo(this);
    }
}
