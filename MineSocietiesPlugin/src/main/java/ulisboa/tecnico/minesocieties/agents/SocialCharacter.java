package ulisboa.tecnico.minesocieties.agents;

import ulisboa.tecnico.agents.ICharacter;
import ulisboa.tecnico.minesocieties.agents.npc.state.CharacterReference;
import ulisboa.tecnico.minesocieties.agents.observation.ISocialObserver;

public abstract class SocialCharacter implements ICharacter, ISocialObserver {
    public CharacterReference toReference() {
        return new CharacterReference(this);
    }
}
