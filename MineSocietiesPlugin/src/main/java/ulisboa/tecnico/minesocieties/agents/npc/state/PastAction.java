package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.minesocieties.agents.actions.ISocialAction;
import ulisboa.tecnico.minesocieties.visitors.PastActionExplainer;

import java.time.Instant;
import java.util.Objects;

public class PastAction extends InstantMemory {

    // Private attributes

    private String pastAction;

    // Storing this explainer here since it's only used here and has no state. No need to create new instances before visiting
    private static final PastActionExplainer PAST_ACTION_EXPLAINER = new PastActionExplainer();

    // Constructors

    public PastAction(ISocialAction action, Instant instant) {
        super(instant);

        this.pastAction = action.accept(PAST_ACTION_EXPLAINER);
    }

    public PastAction(Instant instant, String pastAction) {
        super(instant);

        this.pastAction = pastAction;
    }

    // Getters and setters

    public String getPastAction() {
        return pastAction;
    }

    // Other methods

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PastAction that)) return false;
        if (!super.equals(o)) return false;

        return pastAction.equals(that.pastAction);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + pastAction.hashCode();
        return result;
    }
}
