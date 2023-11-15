package ulisboa.tecnico.minesocieties.agents.actions.exceptions;

public class MalformedActionChoiceException extends Exception {

    // Private attributes

    private final String actionChoice;
    private final String whatWentWrong;

    // Constructors

    public MalformedActionChoiceException(String actionChoice, String whatWentWrong) {
        super(whatWentWrong);

        this.actionChoice = actionChoice;
        this.whatWentWrong = whatWentWrong;
    }

    public MalformedActionChoiceException(Exception cause, String actionChoice, String whatWentWrong) {
        super(whatWentWrong, cause);

        this.actionChoice = actionChoice;
        this.whatWentWrong = whatWentWrong;
    }

    // Getters and setters


    public String getActionChoice() {
        return actionChoice;
    }

    public String getWhatWentWrong() {
        return whatWentWrong;
    }
}
