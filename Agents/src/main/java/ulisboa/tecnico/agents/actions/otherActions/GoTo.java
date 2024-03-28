package ulisboa.tecnico.agents.actions.otherActions;

import org.bukkit.Location;
import org.entityutils.entity.npc.player.MovementStatus;
import ulisboa.tecnico.agents.actions.ActionStatus;
import ulisboa.tecnico.agents.actions.TemporalAction;
import ulisboa.tecnico.agents.npc.IAgent;

public class GoTo<T extends IAgent> extends TemporalAction<T> {

    // Private attributes

    private MovementStatus status = MovementStatus.MOVING;
    private final Location destination;

    // Constructors

    public GoTo(Location destination) {
        this.destination = destination;
    }

    // Getters and setters

    public Location getDestination() {
        return destination;
    }


    // Other methods

    @Override
    public void start(T actioner) {
        actioner.getAgent().goTo(destination, 100, s -> status = s);
    }

    @Override
    public ActionStatus tick(T actioner, int elapsedTicks) {
        return switch (status) {
            case MOVING -> ActionStatus.IN_PROGRESS;
            case SUCCESS -> ActionStatus.SUCCESS;
            case FAILURE -> ActionStatus.FAILURE;
        };
    }

    @Override
    public void cancel(T actioner) {
        actioner.getAgent().cancelMovement();
    }
}
