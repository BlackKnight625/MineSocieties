package ulisboa.tecnico.minesocieties.agents.actions.otherActions;

import org.bukkit.Location;
import ulisboa.tecnico.agents.actions.otherActions.GoTo;
import ulisboa.tecnico.minesocieties.agents.actions.ISocialAction;
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

    // Getters and setters

    public String getDestinationDescription() {
        return destinationDescription;
    }

    // Other methods


    @Override
    public String accept(IActionVisitor visitor) {
        return visitor.visitGoTo(this);
    }
}
