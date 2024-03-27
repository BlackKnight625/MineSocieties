package ulisboa.tecnico.minesocieties.guis.social.locations;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import ulisboa.tecnico.minesocieties.agents.location.LocationReference;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentMemory;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.GUIItem;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenu;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenuOpener;
import ulisboa.tecnico.minesocieties.guis.common.GoBack;

import java.util.Collection;

/**
 *  This menu allows players to see which locations a specific agent has access to and edit them.
 */
public class AgentLocationsMenu extends GUIMenu {

    // Private attributes

    private final SocialAgent agent;

    // Constructors

    public AgentLocationsMenu(SocialPlayer player, SocialAgent agent) {
        super(player, "Locations", 27);

        this.agent = agent;
    }

    // Other methods

    @Override
    public void fillShopWithClickables() {
        AgentMemory memory = agent.getState().getMemory();

        // Adding buttons to edit locations that must exist in the agent's memory
        addClickable(10, new LocationEditItem(this, memory.getHome().getLocation(), true));

        // Adding button to view/edit other locations, which are allowed to be deleted and their number isn't limited
        addClickable(16, new OtherLocationsMenuOpener());

        addClickable(26, new GoBack(this));

        fillRestWithPanes(Material.ORANGE_STAINED_GLASS_PANE);
    }

    public static String toLocationString(LocationReference agentLocation) {
        Vector position = agentLocation.getLocation().getPosition();

        return ChatColor.AQUA + " (" + ChatColor.RED + position.getBlockX() + ChatColor.AQUA + ", " +
                ChatColor.RED + position.getBlockY() + ChatColor.AQUA + ", " +
                ChatColor.RED + position.getBlockZ() + ChatColor.AQUA + ") " +
                ChatColor.GRAY + agentLocation.getName();
    }

    public static void addLocationToDescription(GUIItem item, LocationReference location) {
        String descriptionLine = AgentLocationsMenu.toLocationString(location);

        if (descriptionLine.length() > 46) {
            // Line is too long
            descriptionLine = descriptionLine.substring(0, 47) + "...";
        }

        item.addDescription(descriptionLine);
    }

    public static void addLocationsToDescription(GUIItem item, Collection<LocationReference> locations, int max) {
        int counter = 0;

        for (LocationReference location : locations) {
            AgentLocationsMenu.addLocationToDescription(item, location);
            counter++;

            if (counter > max) {
                // There are too many states
                item.addDescription(ChatColor.BLUE + "Etc...");

                break;
            }
        }
    }

    // Private classes

    private class OtherLocationsMenuOpener extends GUIMenuOpener {

        // Constructors

        public OtherLocationsMenuOpener() {
            super(AgentLocationsMenu.this, Material.MAP, new OtherLocationsMenu(getPlayer(), agent), ChatColor.DARK_AQUA + "Other locations");

            AgentLocationsMenu.addLocationsToDescription(this, agent.getState().getMemory().getKnownLocations().getMemorySections(), 8);
        }
    }
}
