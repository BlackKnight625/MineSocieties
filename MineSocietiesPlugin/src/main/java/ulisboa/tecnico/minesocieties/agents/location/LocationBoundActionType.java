package ulisboa.tecnico.minesocieties.agents.location;

import ulisboa.tecnico.minesocieties.agents.actions.ISocialAction;

import java.util.function.Supplier;

public enum LocationBoundActionType {

    ;

    // Constructors

    LocationBoundActionType(Supplier<ISocialAction> toSocialAction) {
        this.toSocialAction = toSocialAction;
    }

    // Private attributes

    private final Supplier<ISocialAction> toSocialAction;

    // Other methods

    public ISocialAction toSocialAction() {
        return toSocialAction.get();
    }
}
