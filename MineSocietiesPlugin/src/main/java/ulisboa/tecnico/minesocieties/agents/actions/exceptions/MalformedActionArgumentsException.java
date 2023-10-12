package ulisboa.tecnico.minesocieties.agents.actions.exceptions;

public class MalformedActionArgumentsException extends Exception {

    // Private attributes

    private final String arguments;
    private final String whatWentWrong;

    // Constructors

    public MalformedActionArgumentsException(String arguments, String whatWentWrong) {
        this.arguments = arguments;
        this.whatWentWrong = whatWentWrong;
    }

    // Getters and setters

    public String getArguments() {
        return arguments;
    }

    public String getWhatWentWrong() {
        return whatWentWrong;
    }
}
