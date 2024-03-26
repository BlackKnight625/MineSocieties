package ulisboa.tecnico.minesocieties.agents.location;

import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.CharacterReference;

import java.util.Collection;
import java.util.Collections;

public class PersonalAccess extends LocationAccess {

    // Private attributes
    private CharacterReference character;

    // Constructors

    public PersonalAccess(CharacterReference character) {
        super(LocationAccessType.PERSONAL);

        this.character = character;
    }

    public PersonalAccess() {
        super(LocationAccessType.PERSONAL);

        // Constructor for GSON
    }

    // Getters and setters

    public @Nullable CharacterReference getCharacter() {
        return character;
    }

    public void setCharacter(@Nullable CharacterReference character) {
        this.character = character;
    }


    // Other methods

    @Override
    public Collection<CharacterReference> getCharactersWithAccess(SocialLocation location) {
        return character == null ? Collections.emptyList() : Collections.singleton(character);
    }

    @Override
    public boolean hasAccess(SocialAgent agent, SocialLocation location) {
        return character != null && character.getUuid().equals(agent.getUUID());
    }

    @Override
    public boolean isAccessValid() {
        return character != null && character.getReferencedCharacter() != null;
    }

    @Override
    public void fixInconsistencies() {
        // Nothing to do. If the sole agent referenced here becomes invalid, the access becomes invalid.
    }

    @Override
    public Collection<CharacterReference> getStronglyConnectedAgents() {
        return character == null ? Collections.emptyList() : Collections.singleton(character);
    }

    @Override
    public String toString() {
        return "PersonalAccess{" +
                "agent=" + character +
                '}';
    }
}
