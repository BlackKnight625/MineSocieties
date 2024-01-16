package ulisboa.tecnico.minesocieties.guis.common;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

public class GoBack extends GUIItem {

	public GoBack(GUIMenu menu) {
		super(menu, Material.ARROW, "Go back");
		addDescription("Opens the previously opened menu");
	}

	@Override
	public void clicked(ClickType click) {
		if(getMenu().getPreviousMenu() != null) {
			getMenu().getPreviousMenu().open();
		}
		else {
			getMenu().getPlayer().getPlayer().closeInventory();
		}
	}

}
