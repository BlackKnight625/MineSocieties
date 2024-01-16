package ulisboa.tecnico.minesocieties.guis;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *  Brought in from BlackKnight625's Heliomothra code
 */
public abstract class GUIItem implements Clickable {

	/*Private attributes*/
	private ItemStack _item;
	private GUIMenu _menu;
	
	/*Constructors*/
	public GUIItem(GUIMenu menu, Material material, String name) {
		_menu = menu;
		_item = new ItemStack(material);
		
		clearDescription(); //Some items, such as custom ones, already have lore. This will delete it
		setTitle(name);
	}
	
	public GUIItem(GUIMenu menu, ItemStack item, String name) {
		_menu = menu;
		_item = item;
		
		clearDescription(); //Some items, such as custom ones, already have lore. This will delete it
		setTitle(name);
	}
	
	public GUIItem(GUIMenu menu, ItemStack item) {
		_menu = menu;
		_item = item;
	}

	// Constructor used for items in the hotbar
	public GUIItem() {
		_menu = null;
	}
	
	/*Getters and setters*/
	public GUIMenu getMenu() {
		return _menu;
	}

	public void setMenu(GUIMenu menu) {
		this._menu = menu;
	}

	@Override
	public String getTitle() {
		return _item.getItemMeta().getDisplayName();
	}
	
	public void setTitle(String title) {
		ItemMeta meta = _item.getItemMeta();
		
		meta.setDisplayName(title);
		_item.setItemMeta(meta);
	}
	
	@Override
	public List<String> getDescription() {
		return _item.getItemMeta().getLore();
	}
	
	/**
	 * Adds the description of this ShopItem into the ItemStack's lore. If the description contains '\n' chars, they will be accordingly split into
	 * new lore lines.
	 * @param newLineColor
	 * The starting color of each line in the description
	 * @param description
	 * The item's description
	 */
	public void addDescription(ChatColor newLineColor, Collection<String> description) {
		ItemMeta meta = _item.getItemMeta();
		List<String> lines = meta.hasLore() ? meta.getLore() : new ArrayList<>();
		
		for(String descriptionLine : description) {
			if(descriptionLine != null) {
				lines.addAll(Arrays.asList(descriptionLine.split("\n")));
			}
		}
		
		for(int i = 0; i < lines.size(); i++) {
			//Reseting the chat color at the beggining of every line due to the ugly looking italic and purple default colors
			lines.set(i, ChatColor.RESET + "" + newLineColor + lines.get(i));
		}
		
		meta.setLore(lines);
		_item.setItemMeta(meta);
	}

	public void addDescription(ChatColor newLineColor, String... description) {
		addDescription(newLineColor, Arrays.asList(description));
	}
	
	/**
	 * Adds the description of this ShopItem into the ItemStack's lore. If the description contains '\n' chars, they will be accordingly split into
	 * new lore lines.
	 * @param description
	 * The item's description
	 */
	public void addDescription(String... description) {
		addDescription(ChatColor.WHITE, description);
	}

	public void addDescription(Collection<String> description) {
		addDescription(ChatColor.WHITE, description);
	}

	public void removeDescriptionLine(int line) {
		ItemMeta meta = _item.getItemMeta();
		var lore = meta.getLore();
		lore.remove(line);
		meta.setLore(lore);
		_item.setItemMeta(meta);
	}

	public void removeFirstDescriptionLine() {
		removeDescriptionLine(0);
	}

	public void removeLastDescriptionLine() {
		removeDescriptionLine(_item.getItemMeta().getLore().size() - 1);
	}
	
	public void clearDescription() {
		ItemMeta meta = _item.getItemMeta();
		
		meta.setLore(new ArrayList<>());
		
		_item.setItemMeta(meta);
	}
	
	@Override
	public ItemStack getItemStack() {
		return _item;
	}
	
	public void setItemStack(ItemStack item) {
		_item = item;
	}
	
	public SocialPlayer getShopper() {
		return getMenu().getPlayer();
	}
	
	/*Other methods*/

	public abstract void clicked(ClickType click);

	@Override
	public void click(ClickType click) {
		try {
			clicked(click);
		} catch (Exception e) {
			ErrorMenu.error(_menu.getPlayer(), _menu, e,
					"An error occurred while trying to perform the clicking action of the item you clicked.");
		}
	}
}
