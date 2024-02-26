package ulisboa.tecnico.minesocieties.guis.social.information.locations;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentLocation;
import ulisboa.tecnico.minesocieties.guis.common.GUIItem;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenu;

public class CoordinateSelectorItem extends GUIItem {

    // Private attributes

    private final SocialAgent agent;
    private final AgentLocation location;

    // Constructors

    public CoordinateSelectorItem(GUIMenu menu, SocialAgent agent, AgentLocation location) {
        super(menu, Material.RECOVERY_COMPASS, ChatColor.AQUA + "Click " + ChatColor.GRAY + "to receive an item");

        this.agent = agent;
        this.location = location;

        addDescription(ChatColor.GRAY,
                "that allows you to select",
                "the coordinates of this",
                "location by right-clicking",
                "a block in the world.");
    }

    // Other methods

    @Override
    public void clicked(ClickType click) {
        MineSocieties.getPlugin().getGuiManager().giveCoordinatesSelector(getMenu().getPlayer(), agent, location);

        // Closing this menu
        getMenu().getPlayer().getPlayer().closeInventory();
    }
}
