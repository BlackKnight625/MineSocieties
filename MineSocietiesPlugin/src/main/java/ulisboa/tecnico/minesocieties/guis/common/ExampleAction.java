package ulisboa.tecnico.minesocieties.guis.common;

import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ExampleAction extends GUIItem {

	public ExampleAction(GUIMenu menu) {
		super(menu, Material.STICK, "Buy sticks");
		
		addDescription("Cost: " + ChatColor.ITALIC + "It's free real-estate");
	}

	@Override
	public void clicked(ClickType click) {
		getShopper().getPlayer().getInventory().addItem(new ItemStack(Material.STICK));

		getShopper().getPlayer().playNote(getShopper().getPlayer().getLocation(), Instrument.BELL, Note.natural(0, Tone.A));
	}
}
