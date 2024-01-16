package ulisboa.tecnico.minesocieties.guis;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

public class NextPage extends GUIItem {

    // Constructors

    public NextPage(PageableMenu menu) {
        super(menu, Material.SPECTRAL_ARROW, "Next page");

        addDescription("Go to the next page", "of this menu.");
    }

    // Other methods

    @Override
    public void clicked(ClickType click) {
        ((PageableMenu)getMenu()).nextPage();
    }
}
