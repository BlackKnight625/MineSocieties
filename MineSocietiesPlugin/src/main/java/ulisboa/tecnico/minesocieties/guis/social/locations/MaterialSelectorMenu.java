package ulisboa.tecnico.minesocieties.guis.social.locations;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.location.SocialLocation;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.GUIItem;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenu;
import ulisboa.tecnico.minesocieties.guis.common.GoBack;

public class MaterialSelectorMenu extends GUIMenu {

    // Private attributes

    private final SocialLocation location;

    // Constructors

    public MaterialSelectorMenu(SocialPlayer player, SocialLocation location) {
        super(player, "Choose a material", 54);

        this.location = location;
    }

    // Other methods

    @Override
    public void fillShopWithClickables() {
        addClickable(10, new MaterialSelector(Material.RECOVERY_COMPASS));
        addClickable(11, new MaterialSelector(Material.COMPASS));
        addClickable(12, new MaterialSelector(Material.LODESTONE));
        addClickable(13, new MaterialSelector(Material.BLACK_BED));
        addClickable(14, new MaterialSelector(Material.RED_BED));
        addClickable(15, new MaterialSelector(Material.GREEN_BED));
        addClickable(16, new MaterialSelector(Material.YELLOW_BED));
        addClickable(19, new MaterialSelector(Material.FISHING_ROD));
        addClickable(20, new MaterialSelector(Material.IRON_HOE));
        addClickable(21, new MaterialSelector(Material.IRON_SWORD));
        addClickable(22, new MaterialSelector(Material.IRON_PICKAXE));
        addClickable(23, new MaterialSelector(Material.IRON_AXE));
        addClickable(24, new MaterialSelector(Material.FLINT_AND_STEEL));
        addClickable(25, new MaterialSelector(Material.IRON_HORSE_ARMOR));
        addClickable(28, new MaterialSelector(Material.BOOKSHELF));
        addClickable(29, new MaterialSelector(Material.POPPY));
        addClickable(30, new MaterialSelector(Material.PAPER));
        addClickable(31, new MaterialSelector(Material.GLASS_BOTTLE));
        addClickable(32, new MaterialSelector(Material.BREAD));
        addClickable(33, new MaterialSelector(Material.IRON_INGOT));
        addClickable(34, new MaterialSelector(Material.DIAMOND));

        addClickable(53, new GoBack(this));

        fillRestWithPanes(Material.BROWN_STAINED_GLASS_PANE);
    }

    // Private classes

    private class MaterialSelector extends GUIItem {

        // Constructors

        public MaterialSelector(Material material) {
            super(MaterialSelectorMenu.this, material, ChatColor.BLUE + "Material: " + ChatColor.GRAY);

            if (location.getGuiMaterial() == material) {
                // This material is selected
                addDescription("");
                addDescription(ChatColor.GREEN, "Selected!");

                makeItemGlow();
            } else {
                addDescription("");
                addDescription(ChatColor.DARK_GREEN, "Click to select");
            }
        }

        @Override
        public void clicked(ClickType click) {
            if (location.getGuiMaterial() != getItemStack().getType()) {
                // This material is not selected. Setting it
                location.setGuiMaterial(getItemStack().getType());

                MineSocieties.getPlugin().getLocationsManager().saveAsync(location);

                getPlayer().getPlayer().playSound(getPlayer().getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

                hardReset();
            }
        }
    }
}
