package ulisboa.tecnico.minesocieties.guis.social.information.locations;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentMemory;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenuOpener;
import ulisboa.tecnico.minesocieties.guis.social.information.AgentInformationMenu;

public class LocationsItem extends GUIMenuOpener {

    // Constructors

    public LocationsItem(AgentInformationMenu menu, SocialAgent agent) {
        super(menu, Material.COMPASS, new LocationsMenu(menu.getPlayer(), agent), ChatColor.GOLD + "Known locations");

        // Adding some states to the description
        LocationsMenu.addLocationsToDescription(this, agent.getState().getAllLocations(), 8);

        addDescription("");
        addDescription(ChatColor.GREEN + "Click to edit/view more details");
    }
}
