package ulisboa.tecnico.minesocieties.guis.social.information.locations;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentMemory;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenu;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenuOpener;

public class LocationsMenu extends GUIMenu {

    // Private attributes

    private final SocialAgent agent;

    // Constructors

    public LocationsMenu(SocialPlayer player, SocialAgent agent) {
        super(player, "Locations", 27);

        this.agent = agent;
    }

    // Other methods

    @Override
    public void fillShopWithClickables() {
        AgentMemory memory = agent.getState().getMemory();

        // Adding buttons to edit locations that must exist in the agent's memory
        addClickable(10, new LocationEditItem(this, Material.RED_BED, memory.getHome(), true));
        // TODO: Add Job location and maybe others

        // Adding button to view/edit other locations, which are allowed to be deleted and their number isn't limited
        addClickable(16, new OtherLocationsMenuOpener());
    }

    // Private classes

    private class OtherLocationsMenuOpener extends GUIMenuOpener {

        // Constructors

        public OtherLocationsMenuOpener() {
            super(LocationsMenu.this, Material.MAP, new OtherLocationsMenu(getPlayer(), agent), ChatColor.DARK_AQUA + "Other locations");
        }
    }
}
