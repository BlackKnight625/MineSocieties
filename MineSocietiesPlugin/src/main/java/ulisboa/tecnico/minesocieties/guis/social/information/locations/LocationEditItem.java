package ulisboa.tecnico.minesocieties.guis.social.information.locations;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.location.SocialLocation;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenu;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenuOpener;
import ulisboa.tecnico.minesocieties.utils.StringUtils;

public class LocationEditItem extends GUIMenuOpener {

    // Private attributes

    private final boolean editingIsLimited;
    private final SocialLocation location;

    // Constructors

    public LocationEditItem(GUIMenu menu, SocialLocation location, boolean editingIsLimited) {
        super(menu, location.getGuiMaterial(),
                new LocationEditorMenu(menu.getPlayer(), location, editingIsLimited),
                ChatColor.YELLOW + toCoordinates(location));

        this.location = location;
        this.editingIsLimited = editingIsLimited;

        addDescription(ChatColor.GRAY, StringUtils.splitIntoLines(location.getName(), 30));
        addDescription("");

        if (!editingIsLimited) {
            // Locations like this can be deleted
            addDescription(ChatColor.RED, "Right-click to delete");
        }

        addDescription(ChatColor.GREEN, "Left-click to edit");
    }

    @Override
    public void clicked(ClickType click) {
        if (click.isLeftClick()) {
            // Opening the menu to edit this location
            super.clicked(click);
        } else if (click.isRightClick() && !editingIsLimited) {
            // Deleting this location
            MineSocieties.getPlugin().getLocationsManager().deleteAsync(location);

            Player player = getMenu().getPlayer().getPlayer();

            player.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);

            getMenu().hardReset();
        }
    }

    private static String toCoordinates(SocialLocation location) {
        return ChatColor.YELLOW + "X: " + ChatColor.GRAY + location.getPosition().getBlockX() +
                ChatColor.YELLOW + " Y: " + ChatColor.GRAY + location.getPosition().getBlockY() +
                ChatColor.YELLOW + " Z: " + ChatColor.GRAY + location.getPosition().getBlockZ() +
                ChatColor.AQUA + " " + location.getWorldName();
    }
}
