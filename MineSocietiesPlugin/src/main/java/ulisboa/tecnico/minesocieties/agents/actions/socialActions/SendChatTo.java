package ulisboa.tecnico.minesocieties.agents.actions.socialActions;

import ulisboa.tecnico.agents.actions.ActionStatus;
import ulisboa.tecnico.agents.actions.IAction;
import ulisboa.tecnico.minesocieties.agents.SocialCharacter;
import ulisboa.tecnico.minesocieties.agents.actions.IActionWithArguments;
import ulisboa.tecnico.minesocieties.agents.actions.IExplainableAction;
import ulisboa.tecnico.minesocieties.agents.actions.exceptions.MalformedActionArgumentsException;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.visitors.IActionArgumentsExplainerVisitor;
import ulisboa.tecnico.minesocieties.visitors.IActionExplainerVisitor;

public class SendChatTo implements IAction<SocialAgent>, IExplainableAction, IActionWithArguments {

    // Private attributes

    private String message;
    private SocialCharacter receiver;

    // Getters and setters

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SocialCharacter getReceiver() {
        return receiver;
    }

    public void setReceiver(SocialCharacter receiver) {
        this.receiver = receiver;
    }

    // Other methods


    @Override
    public void acceptArgumentsInterpreter(IActionArgumentsExplainerVisitor visitor, String arguments) throws MalformedActionArgumentsException {
        visitor.setArgumentsOfSendChatTo(this, arguments);
    }

    @Override
    public String acceptArgumentsExplainer(IActionArgumentsExplainerVisitor visitor) {
        return visitor.explainSendChatToArguments(this);
    }

    @Override
    public ActionStatus act(SocialAgent actioner) {
        return null;
    }

    @Override
    public String accept(IActionExplainerVisitor visitor) {
        return null;
    }
}
