package ulisboa.tecnico.minesocieties.guis.social.locations;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.GUIItem;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenu;

public class MaterialSelectorMenu extends GUIMenu {

    // Private attributes

    private final LocationEditorMenu editorMenu;

    // Constructors

    public MaterialSelectorMenu(SocialPlayer player, LocationEditorMenu editorMenu) {
        super(player, "Choose a material", 54);

        this.editorMenu = editorMenu;
    }

    // Other methods

    @Override
    public void fillShopWithClickables() {

    }

    // Private classes

    private class MaterialSelector extends GUIItem {

        // Constructors

        public MaterialSelector(GUIMenu menu, Material material, String name) {
            super(menu, material, ChatColor.BLUE + "Material: " + ChatColor.GRAY);
        }

        @Override
        public void clicked(ClickType click) {

        }
    }
}
