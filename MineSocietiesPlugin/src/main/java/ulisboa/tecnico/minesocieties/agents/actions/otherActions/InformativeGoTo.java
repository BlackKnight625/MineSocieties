package ulisboa.tecnico.minesocieties.agents.actions.otherActions;

import org.bukkit.Location;
import ulisboa.tecnico.agents.actions.otherActions.GoTo;
import ulisboa.tecnico.minesocieties.agents.actions.IExplainableAction;
import ulisboa.tecnico.minesocieties.visitors.IActionExplainerVisitor;

public class InformativeGoTo extends GoTo implements IExplainableAction {

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
    public void accept(IActionExplainerVisitor visitor) {
        visitor.visitGoTo(this);
    }
}
