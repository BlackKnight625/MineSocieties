package ulisboa.tecnico.minesocieties.agents.location;

import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.CharacterReference;

import java.util.Collection;
import java.util.Collections;

public class PersonalAccess extends LocationAccess {

    // Private attributes
    private final CharacterReference character;

    // Constructors

    public PersonalAccess(CharacterReference character) {
        this.character = character;
    }

    // Other methods

    @Override
    public Collection<CharacterReference> getCharactersWithAccess(SocialLocation location) {
        return Collections.singleton(character);
    }

    @Override
    public boolean hasAccess(SocialAgent agent, SocialLocation location) {
        return character.getUuid().equals(agent.getUUID());
    }

    @Override
    public String toString() {
        return "PersonalAccess{" +
                "agent=" + character +
                '}';
    }
}
