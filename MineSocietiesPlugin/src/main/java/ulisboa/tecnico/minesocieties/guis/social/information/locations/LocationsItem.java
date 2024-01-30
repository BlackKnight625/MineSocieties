package ulisboa.tecnico.minesocieties.guis.social.information.locations;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentLocation;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentMemory;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenuOpener;
import ulisboa.tecnico.minesocieties.guis.social.information.AgentInformationMenu;

public class LocationsItem extends GUIMenuOpener {

    // Constructors

    public LocationsItem(AgentInformationMenu menu, SocialAgent agent) {
        super(menu, Material.FILLED_MAP, new LocationsMenu(menu.getPlayer(), agent), ChatColor.GOLD + "Known locations");

        int counter = 0;

        AgentMemory memory = agent.getState().getMemory();

        addDescription(toLocationString(memory.getHome()));

        // Adding some states to the description
        for (AgentLocation location : memory.getKnownLocations().getMemorySections()) {
            String descriptionLine = toLocationString(location);

            if (descriptionLine.length() > 30) {
                // Line is too long
                descriptionLine = descriptionLine.substring(0, 31) + "...";
            }

            addDescription(descriptionLine);
            counter++;

            if (counter > 8) {
                // There are too many states
                addDescription(ChatColor.DARK_BLUE + "Etc...");

                break;
            }
        }

        addDescription("");
        addDescription(ChatColor.GREEN + "Click to edit/view more details");
    }

    private String toLocationString(AgentLocation agentLocation) {
        Vector position = agentLocation.getPosition();

        return ChatColor.AQUA + " (" + ChatColor.RED + position.getBlockX() + ChatColor.AQUA + ", " +
                ChatColor.RED + position.getBlockY() + ChatColor.AQUA + ", " +
                ChatColor.RED + position.getBlockZ() + ChatColor.AQUA + ") " +
                ChatColor.GRAY + agentLocation.getDescription();
    }
}
