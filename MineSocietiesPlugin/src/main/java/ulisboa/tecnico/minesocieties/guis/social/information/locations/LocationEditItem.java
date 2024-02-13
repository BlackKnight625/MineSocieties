package ulisboa.tecnico.minesocieties.guis.social.information.locations;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentLocation;
import ulisboa.tecnico.minesocieties.guis.common.GUIItem;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenu;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenuOpener;
import ulisboa.tecnico.minesocieties.utils.StringUtils;

public class LocationEditItem extends GUIMenuOpener {

    // Private attributes

    private final SocialAgent agent;
    private final boolean editingIsLimited;
    private final AgentLocation location;

    // Constructors

    public LocationEditItem(GUIMenu menu, Material material, SocialAgent agent, AgentLocation location, boolean editingIsLimited) {
        super(menu, material,
                new LocationEditorMenu(menu.getPlayer(), agent, location, editingIsLimited),
                ChatColor.YELLOW + toCoordinates(location));

        this.agent = agent;
        this.location = location;
        this.editingIsLimited = editingIsLimited;

        addDescription(ChatColor.GRAY, StringUtils.splitIntoLines(location.getDescription(), 30));
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
            agent.getState().getMemory().getKnownLocations().remove(location);
            agent.getState().saveAsync();

            Player player = getMenu().getPlayer().getPlayer();

            player.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);

            getMenu().hardReset();
        }
    }

    private static String toCoordinates(AgentLocation location) {
        return ChatColor.YELLOW + "X: " + ChatColor.GRAY + location.getPosition().getBlockX() +
                ChatColor.YELLOW + " Y: " + ChatColor.GRAY + location.getPosition().getBlockY() +
                ChatColor.YELLOW + " Z: " + ChatColor.GRAY + location.getPosition().getBlockZ() +
                ChatColor.AQUA + " " + location.getWorldName();
    }
}
