package ulisboa.tecnico.agents.actions;

import ulisboa.tecnico.agents.ICharacter;

public interface IAction<T extends ICharacter> {

    ActionStatus act(T actioner);
}
