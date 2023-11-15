package ulisboa.tecnico.agents.actions;

public enum ActionStatus {
    SUCCESS(true),
    IN_PROGRESS(false),
    FAILURE(true),
    ;

    // Private attributes

    private final boolean finished;

    // Constructors

    ActionStatus(boolean finished) {
        this.finished = finished;
    }

    // Getters and setters

    public boolean isFinished() {
        return finished;
    }
}
