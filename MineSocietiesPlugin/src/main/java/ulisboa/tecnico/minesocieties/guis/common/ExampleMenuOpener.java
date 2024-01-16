package ulisboa.tecnico.minesocieties.guis.common;

import org.bukkit.Material;

public class ExampleMenuOpener extends GUIMenuOpener {

	public ExampleMenuOpener(GUIMenu menu) {
		super(menu, Material.OAK_PLANKS, new ExampleSubMenu(menu.getPlayer()), "Wood shop");
		
		addDescription("For really cheap wood");
	}

}
