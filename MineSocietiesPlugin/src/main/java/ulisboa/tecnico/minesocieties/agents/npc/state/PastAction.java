package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.minesocieties.agents.actions.ISocialAction;
import ulisboa.tecnico.minesocieties.visitors.PastActionExplainer;

import java.time.Instant;

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

    // Getters and setters

    public String getPastAction() {
        return pastAction;
    }
}
