package ulisboa.tecnico.agents.actions;

public enum ActionStatus {
    SUCCESS(true, true),
    IN_PROGRESS(false, false),
    FAILURE(true, false),
    ;

    // Private attributes

    private final boolean finished;
    private final boolean successful;

    // Constructors

    ActionStatus(boolean finished, boolean successful) {
        this.finished = finished;
        this.successful = successful;
    }

    // Getters and setters

    public boolean isFinished() {
        return finished;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
