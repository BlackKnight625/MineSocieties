package ulisboa.tecnico.minesocieties.guis.social.locations;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitRunnable;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.location.LocationBoundActionType;
import ulisboa.tecnico.minesocieties.agents.location.SocialLocation;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.*;
import ulisboa.tecnico.minesocieties.utils.StringUtils;

public class LocationEditorMenu extends GUIMenu {

    // Private attributes

    private final SocialLocation location;
    private final boolean editingIsLimited;

    // Constructors

    /**
     *  This constructor should be used when editing an existing location
     * @param player
     *  The player that's editing the location
     * @param location
     *  The location being edited
     * @param editingIsLimited
     *  If true, the location cannot be deleted and its description cannot be edited
     */
    public LocationEditorMenu(SocialPlayer player, SocialLocation location, boolean editingIsLimited) {
        super(player, "Location editor", 36);

        this.location = location;
        this.editingIsLimited = editingIsLimited;
    }

    @Override
    public void fillShopWithClickables() {
        // Buttons to edit coordinates
        addClickable(9, new XEditor());
        addClickable(10, new YEditor());
        addClickable(11, new ZEditor());

        // Description and world editor

        if (!editingIsLimited) {
            addClickable(13, new NameEditor());
            addClickable(22, new MaterialEditor());
        }

        addClickable(14, new WorldNameEditor());
        addClickable(23, new AccessEditor());

        addClickable(19, new CoordinateSelectorItem(this, location));

        addClickable(16, new ActionsEditor());

        addClickable(35, new GoBack(this));

        fillRestWithPanes(Material.PURPLE_STAINED_GLASS_PANE);
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

                            double coordinate = Double.parseDouble(lines.get(0));
                            Player player = getPlayer().getPlayer();

                            if (isValidCoordinate(coordinate)) {
                                setCoordinate(coordinate);

                                MineSocieties.getPlugin().getLocationsManager().saveAsync(location);

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

        public abstract void setCoordinate(double coordinate);

        public boolean isValidCoordinate(double coordinate) {
            return true;
        }
    }

    private class XEditor extends CoordinateEditor {

        // Constructors

        public XEditor() {
            super(Material.RED_WOOL, ChatColor.DARK_RED + "X: " + ChatColor.GRAY + location.getPosition().getX());
        }

        // Other methods

        @Override
        public void setCoordinate(double coordinate) {
            location.getPosition().setX(coordinate);
        }
    }

    private class YEditor extends CoordinateEditor {

        // Constructors

        public YEditor() {
            super(Material.GREEN_WOOL, ChatColor.DARK_GREEN + "Y: " + ChatColor.GRAY + location.getPosition().getY());
        }

        // Other methods

        @Override
        public void setCoordinate(double coordinate) {
            location.getPosition().setY(coordinate);
        }

        @Override
        public boolean isValidCoordinate(double coordinate) {
            World world = getPlayer().getLocation().getWorld();

            return world.getMinHeight() >= -64 && coordinate < world.getMaxHeight();
        }
    }

    private class ZEditor extends CoordinateEditor {

        // Constructors

        public ZEditor() {
            super(Material.BLUE_WOOL, ChatColor.BLUE + "Z: " + ChatColor.GRAY + location.getPosition().getZ());
        }

        // Other methods

        @Override
        public void setCoordinate(double coordinate) {
            location.getPosition().setZ(coordinate);
        }
    }

    private class NameEditor extends GUIItem {

        // Constructors

        public NameEditor() {
            super(LocationEditorMenu.this, Material.WRITABLE_BOOK, ChatColor.YELLOW + "Location's name");

            addDescription(ChatColor.GRAY, StringUtils.splitIntoLines(location.getName(), 30));
            addDescription(""); // Empty line
            addDescription(ChatColor.GREEN, "Click to edit");
        }

        // Other methods

        @Override
        public void clicked(ClickType click) {
            // Closing this menu so the player may type in the book
            getPlayer().getPlayer().getOpenInventory().close();

            // Giving the player a book to type the location's description
            MineSocieties.getPlugin().getGuiManager().giveCustomEditingBook(getPlayer(), newDescriptionPages -> {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        String newName = newDescriptionPages.stream().reduce("", (a, b) -> a + b);

                        if (newName.length() > 256) {
                            new ErrorMenu(getPlayer(), "The name is too long. Max is 256 characters.", LocationEditorMenu.this).open();

                            return;
                        }

                        location.setName(newName);
                        MineSocieties.getPlugin().getLocationsManager().saveAsync(location);

                        open();
                    }
                }.runTask(MineSocieties.getPlugin());
            }, "Insert the new description for the location");
        }
    }

    private class WorldNameEditor extends GUIItem {

        // Constructors

        public WorldNameEditor() {
            super(LocationEditorMenu.this, Material.GRASS_BLOCK, ChatColor.DARK_AQUA + "World name");

            addDescription(ChatColor.AQUA, "Current world:");
            addDescription(ChatColor.GRAY, location.getWorldName());
            addDescription(""); // Empty line
            addDescription(ChatColor.GREEN, "Click to edit");
        }

        // Other methods

        @Override
        public void clicked(ClickType click) {
            MineSocieties.getPlugin().getGuiManager().openSignGUI(getPlayer(), lines -> {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (lines.isEmpty()) {
                            new ErrorMenu(getPlayer(), "You must insert a world name", LocationEditorMenu.this).open();

                            return;
                        }

                        String worldName = lines.stream().reduce("", (a, b) -> a + b);

                        // Checking if the world exists

                        if (MineSocieties.getPlugin().getServer().getWorld(worldName) == null) {
                            new ErrorMenu(getPlayer(), "The world (" + worldName + ") doesn't exist", LocationEditorMenu.this).open();

                            return;
                        }

                        location.setWorldName(worldName);
                        MineSocieties.getPlugin().getLocationsManager().saveAsync(location);

                        open();
                    }
                }.runTask(MineSocieties.getPlugin());
            });
        }
    }

    private class AccessEditor extends GUIMenuOpener {

        // Constructors

        public AccessEditor() {
            super(LocationEditorMenu.this, location.getAccess().getGuiMaterial(),
                    new AccessEditMenu(getPlayer(), location, editingIsLimited), ChatColor.YELLOW + location.getAccess().getGuiName());

            // Showing information regarding this access

            addDescription(ChatColor.GRAY, location.getAccess().getGuiDescriptionLines());

            addDescription(""); // Empty line
            addDescription(ChatColor.GREEN, "Click to edit");
        }
    }

    private class MaterialEditor extends GUIMenuOpener {

        // Constructors

        public MaterialEditor() {
            super(LocationEditorMenu.this, location.getGuiMaterial(), new MaterialSelectorMenu(getPlayer(), location), ChatColor.DARK_GREEN + "Location's item");

            addDescription(ChatColor.GRAY,
                    "Purely cosmetic. The purpose is ",
                    "to make it easier to identify ",
                    "the location in the GUI."
            );
        }
    }

    private class ActionsEditor extends GUIMenuOpener {

        // Constructors

        public ActionsEditor() {
            super(LocationEditorMenu.this, Material.REDSTONE, new LocationBoundActionMenu(getPlayer(), location), ChatColor.BLUE + "Location's actions");

            addDescription(ChatColor.GRAY,
                    "You may choose actions that NPCs",
                    "can perform if they're close to",
                    "this location."
            );

            addDescription(ChatColor.AQUA + "Current actions:");

            // Showing all selected actions
            addDescription(ChatColor.BLUE, location.getPossibleActionTypes().stream().map(LocationBoundActionType::getGuiName).toList());
        }
    }
}
