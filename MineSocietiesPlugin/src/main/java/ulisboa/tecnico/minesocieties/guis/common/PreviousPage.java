package ulisboa.tecnico.minesocieties.guis.common;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

public class PreviousPage extends GUIItem {

    // Constructors

    public PreviousPage(PageableMenu menu) {
        super(menu, Material.SPECTRAL_ARROW, "Previous page");

        addDescription("Go to the previous page", "of this menu.");
    }

    // Other methods

    @Override
    public void clicked(ClickType click) {
        ((PageableMenu)getMenu()).previousPage();
    }
}
