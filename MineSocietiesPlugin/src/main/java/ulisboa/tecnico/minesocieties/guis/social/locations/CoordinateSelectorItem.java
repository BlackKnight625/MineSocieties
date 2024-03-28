package ulisboa.tecnico.minesocieties.guis.social.locations;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.location.SocialLocation;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.guis.common.GUIItem;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenu;

public class CoordinateSelectorItem extends GUIItem {

    // Private attributes

    private final SocialLocation location;

    // Constructors

    public CoordinateSelectorItem(GUIMenu menu, SocialLocation location) {
        super(menu, Material.RECOVERY_COMPASS, ChatColor.AQUA + "Click " + ChatColor.GRAY + "to receive an item");

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
        MineSocieties.getPlugin().getGuiManager().giveCoordinatesSelector(getMenu().getPlayer(), location);

        // Closing this menu
        getMenu().getPlayer().getPlayer().closeInventory();
    }
}