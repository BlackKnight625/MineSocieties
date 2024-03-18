package ulisboa.tecnico.minesocieties.agents.location;

import ulisboa.tecnico.minesocieties.agents.actions.ISocialAction;
import ulisboa.tecnico.minesocieties.agents.actions.jobActions.InformativeGoFishing;

import java.util.function.Supplier;

public enum LocationBoundActionType {
    FISHING(() -> new InformativeGoFishing(5, 3 * 60 * 20, 60 * 20)),
    ;

    // Constructors

    LocationBoundActionType(Supplier<ISocialAction> toSocialAction) {
        this.toSocialAction = toSocialAction;
    }

    // Private attributes

    private final Supplier<ISocialAction> toSocialAction;

    // Other methods

    public ISocialAction toNewSocialAction() {
        return toSocialAction.get();
    }
}
