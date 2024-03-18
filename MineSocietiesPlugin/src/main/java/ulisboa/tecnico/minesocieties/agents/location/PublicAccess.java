package ulisboa.tecnico.minesocieties.agents.location;

import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.CharacterReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PublicAccess extends LocationAccess {

    // Constructors

    public PublicAccess() {
        super(LocationAccessType.PUBLIC);
    }

    // Other methods

    @Override
    public Collection<CharacterReference> getCharactersWithAccess(SocialLocation location) {
        List<CharacterReference> characters = new ArrayList<>();

        // Adding all valid agents
        MineSocieties.getPlugin().getSocialAgentManager().forEachValidAgent(agent -> characters.add(new CharacterReference(agent)));

        return characters;
    }

    @Override
    public boolean hasAccess(SocialAgent agent, SocialLocation location) {
        return true;
    }

    @Override
    public boolean isValid() {
        return true; // Public locations are always valid
    }

    @Override
    public void fixInconsistencies() {
        // Nothing to do. This access means everyone knows about this location
    }

    @Override
    public String toString() {
        return "PublicAccess{}";
    }
}
