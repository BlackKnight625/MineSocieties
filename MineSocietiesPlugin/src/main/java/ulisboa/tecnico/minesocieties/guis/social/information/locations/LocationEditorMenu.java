package ulisboa.tecnico.minesocieties.guis.social.information.locations;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentLocation;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.ErrorMenu;
import ulisboa.tecnico.minesocieties.guis.common.GUIItem;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenu;
import ulisboa.tecnico.minesocieties.guis.common.GoBack;

import java.util.function.Predicate;

public class LocationEditorMenu extends GUIMenu {

    // Private attributes

    private final SocialAgent agent;
    private final AgentLocation location;

    // Constructors

    public LocationEditorMenu(SocialPlayer player, SocialAgent agent, AgentLocation location) {
        super(player, "Location editor", 27);

        this.agent = agent;
        this.location = location;
    }

    /**
     *  This constructor should be used when a new location is being created
     * @param player
     *  The player that's creating the location
     * @param agent
     *  The agent who will have this location
     */
    public LocationEditorMenu(SocialPlayer player, SocialAgent agent) {
        this(player, agent, new AgentLocation(player.getLocation().toVector(), player.getLocation().getWorld().getName(), "New location"));

        // Saving the new unedited location
        agent.getState().getMemory().getKnownLocations().addMemorySection(location);
        agent.getState().saveAsync();
    }

    @Override
    public void fillShopWithClickables() {
        // Buttons to edit coordinates
        addClickable(10, new XEditor());
        addClickable(11, new YEditor());
        addClickable(12, new ZEditor());



        fillRestWithPanes(Material.PURPLE_STAINED_GLASS_PANE);

        addClickable(26, new GoBack(this));
    }

    // Private classes

    private abstract class CoordinateEditor extends GUIItem {

        // Private attributes

        // Constructors

        public CoordinateEditor(Material material, String name) {
            super(LocationEditorMenu.this, material, name);
        }

        // Other methods

        @Override
        public void clicked(ClickType click) {
            // Opening a sign so the player may insert the new coordinate
            MineSocieties.getPlugin().getGuiManager().openSignGUI(getPlayer(), lines -> {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            if (lines.isEmpty()) {
                                new ErrorMenu(getPlayer(), "You must insert a number", LocationEditorMenu.this).open();

                                return;
                            }

                            int coordinate = Integer.parseInt(lines.get(0));
                            Player player = getPlayer().getPlayer();

                            if (isValidCoordinate(coordinate)) {
                                setCoordinate(coordinate);
                                agent.getState().saveAsync();

                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

                                open();
                            } else {
                                new ErrorMenu(getPlayer(), "Invalid coordinate: " + lines.get(0), LocationEditorMenu.this).open();
                            }
                        } catch (NumberFormatException e) {
                            new ErrorMenu(getPlayer(), "Invalid coordinate: " + lines.get(0), LocationEditorMenu.this).open();
                        }
                    }
                }.runTask(MineSocieties.getPlugin());
            });
        }

        public abstract void setCoordinate(int coordinate);

        public boolean isValidCoordinate(int coordinate) {
            return true;
        }
    }

    private class XEditor extends CoordinateEditor {

        // Constructors

        public XEditor() {
            super(Material.RED_WOOL, ChatColor.RED + "X: " + ChatColor.GRAY + location.getPosition().getBlockX());
        }

        // Other methods

        @Override
        public void setCoordinate(int coordinate) {
            location.getPosition().setX(coordinate);
        }
    }

    private class YEditor extends CoordinateEditor {

        // Constructors

        public YEditor() {
            super(Material.GREEN_WOOL, ChatColor.DARK_GREEN + "Y: " + ChatColor.GRAY + location.getPosition().getBlockY());
        }

        // Other methods

        @Override
        public void setCoordinate(int coordinate) {
            location.getPosition().setY(coordinate);
        }

        @Override
        public boolean isValidCoordinate(int coordinate) {
            World world = getPlayer().getLocation().getWorld();

            return world.getMinHeight() >= -64 && coordinate < world.getMaxHeight();
        }
    }

    private class ZEditor extends CoordinateEditor {

        // Constructors

        public ZEditor() {
            super(Material.BLUE_WOOL, ChatColor.BLUE + "Z: " + ChatColor.GRAY + location.getPosition().getBlockZ());
        }

        // Other methods

        @Override
        public void setCoordinate(int coordinate) {
            location.getPosition().setZ(coordinate);
        }
    }

    private class DescriptionEditor extends GUIItem {

        // Constructors

        public DescriptionEditor(GUIMenu menu, Material material, String name) {
            super(LocationEditorMenu.this, material, name);
        }

        // Other methods

        @Override
        public void clicked(ClickType click) {

        }
    }
}
