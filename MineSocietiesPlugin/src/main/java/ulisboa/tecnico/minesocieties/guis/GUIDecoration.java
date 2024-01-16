package ulisboa.tecnico.minesocieties.guis;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a clickeable that does nothing. It serves only as decoratio and/or to display some text.
 */
public class GUIDecoration extends GUIItem {
	/*Constructors*/
	public GUIDecoration(GUIMenu menu, Material material, String name) {
		super(menu, material, name);
	}
	
	public GUIDecoration(GUIMenu menu, ItemStack item, String name) {
		super(menu, item, name);
	}
	
	public GUIDecoration(GUIMenu menu, ItemStack item) {
		super(menu, item);
	}

	/*Other methods*/
	@Override
	public void clicked(ClickType click) {
		//Do nothing. This is purely a decoration
	}
}
