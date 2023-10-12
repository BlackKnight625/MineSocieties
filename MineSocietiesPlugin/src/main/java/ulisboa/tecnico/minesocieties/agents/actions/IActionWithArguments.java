package ulisboa.tecnico.minesocieties.agents.actions;

import ulisboa.tecnico.minesocieties.agents.actions.exceptions.MalformedActionArgumentsException;
import ulisboa.tecnico.minesocieties.visitors.IActionArgumentsExplainerVisitor;

/**
 *  Actions whose arguments should depend on ChatGPT's decisions (such as whom an agent should chat with, and what the
 * contents of the message are) must implement this interface
 */
public interface IActionWithArguments {

    void acceptArgumentsInterpreter(IActionArgumentsExplainerVisitor visitor, String arguments) throws MalformedActionArgumentsException;

    String acceptArgumentsExplainer(IActionArgumentsExplainerVisitor visitor);
}
