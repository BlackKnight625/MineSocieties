package ulisboa.tecnico.minesocieties.guis.common;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class GUIMenuOpener extends GUIItem {
	/*Private attributes*/
	private GUIMenu _menuToOpen;
	
	/*Constructors*/
	public GUIMenuOpener(GUIMenu menu, Material material, GUIMenu menuToOpen, String name) {
		super(menu, material, name);
		
		_menuToOpen = menuToOpen;
	}
	
	public GUIMenuOpener(GUIMenu menu, ItemStack item, GUIMenu menuToOpen, String name) {
		super(menu, item, name);
		
		_menuToOpen = menuToOpen;
	}
	
	public GUIMenuOpener(GUIMenu menu, ItemStack item, GUIMenu menuToOpen) {
		super(menu, item);
		
		_menuToOpen = menuToOpen;
	}

	/*Getters and setters*/
	public GUIMenu getMenuToOpen() {
		return _menuToOpen;
	}

	protected void setMenuToOpen(GUIMenu menuToOpen) {
		this._menuToOpen = menuToOpen;
	}

	/*Other methods*/
	@Override
	public void clicked(ClickType click) {
		_menuToOpen.setPreviousMenu(getMenu());
		_menuToOpen.open();
	}
}
