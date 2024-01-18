package ulisboa.tecnico.minesocieties.guis.social.information;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.util.Vector;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentLocation;
import ulisboa.tecnico.minesocieties.guis.common.GUIItem;
import ulisboa.tecnico.minesocieties.utils.StringUtils;

public class LocationsItem extends GUIItem {

    // Constructors

    public LocationsItem(AgentInformationMenu menu, SocialAgent agent) {
        super(menu, Material.RED_BED, ChatColor.GOLD + "Known locations");

        // TODO - Iterate over the locations and add them to the description
    }

    private String toLocationString(AgentLocation agentLocation) {
        Vector position = agentLocation.getPosition();

        return ChatColor.GRAY + agentLocation.getDescription() +
                ChatColor.AQUA + " (" + ChatColor.RED + position.getBlockX() + ChatColor.AQUA + ", " +
                ChatColor.RED + position.getBlockY() + ChatColor.AQUA + ", " +
                ChatColor.RED + position.getBlockZ() + ChatColor.AQUA + ")";
    }

    @Override
    public void clicked(ClickType click) {
        // TODO Open a menu to add/remove/change the locations
    }
}
