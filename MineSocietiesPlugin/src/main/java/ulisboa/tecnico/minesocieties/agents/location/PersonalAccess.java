package ulisboa.tecnico.minesocieties.agents.location;

import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.CharacterReference;

import java.util.Collection;
import java.util.Collections;

public class PersonalAccess extends LocationAccess {

    // Private attributes
    private final CharacterReference character;

    // Constructors

    public PersonalAccess(CharacterReference character) {
        super(LocationAccessType.PERSONAL);

        this.character = character;
    }

    public PersonalAccess() {
        super(LocationAccessType.PERSONAL);

        // Constructor for GSON
        this.character = null;
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
    public boolean isAccessValid() {
        return character.getReferencedCharacter() != null;
    }

    @Override
    public void fixInconsistencies() {
        // Nothing to do. If the sole agent referenced here becomes invalid, the access becomes invalid.
    }

    @Override
    public Collection<CharacterReference> getStronglyConnectedAgents() {
        return Collections.singleton(character);
    }

    @Override
    public String toString() {
        return "PersonalAccess{" +
                "agent=" + character +
                '}';
    }

    @Override
    public Material getGuiMaterial() {
        return Material.IRON_DOOR;
    }

    @Override
    public String getGuiName() {
        return "Private Acess";
    }

    @Override
    protected String getGuiDescription() {
        return "Only the specified agent can ever know about this location";
    }
}
