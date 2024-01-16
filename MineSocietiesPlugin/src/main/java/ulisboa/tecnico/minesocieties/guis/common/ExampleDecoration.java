package ulisboa.tecnico.minesocieties.guis.common;

import org.bukkit.Material;

public class ExampleDecoration extends GUIDecoration {

	public ExampleDecoration(GUIMenu menu) {
		super(menu, Material.RED_STAINED_GLASS_PANE, "I am a decoration");
		
		addDescription("Clicking me does nothing", "Second line of the description");
	}
}
