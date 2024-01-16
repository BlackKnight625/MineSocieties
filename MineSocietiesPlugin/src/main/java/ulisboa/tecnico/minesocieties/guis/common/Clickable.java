package ulisboa.tecnico.minesocieties.guis.common;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Clickable {
	void click(ClickType click);
	
	String getTitle();
	
	List<String> getDescription();
	
	ItemStack getItemStack();
}
