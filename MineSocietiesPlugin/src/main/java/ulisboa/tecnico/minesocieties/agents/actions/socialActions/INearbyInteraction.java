package ulisboa.tecnico.minesocieties.agents.actions.socialActions;

import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.utils.LocationUtils;

import java.util.List;

/**
 *  Represents an action that can be performed by a SocialAgent that involves interacting with nearby characters.
 */
public interface INearbyInteraction {
    default List<String> getNamesOfNearbyCharacters(SocialAgent agent) {
        return LocationUtils.getNearbyCharacterNamesExcludingSelf(agent.getLocation(), agent.getUUID());
    }
}
