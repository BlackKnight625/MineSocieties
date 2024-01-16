package ulisboa.tecnico.minesocieties.guis.common;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * Represents a GUI menu, that contains things that can get clicked
 *
 * Brought in from BlackKnight625's Heliomothra code
 */
public abstract class GUIMenu {
	/*Private attributes*/
	private HashMap<Integer, Clickable> _clickables = new HashMap<>();
	private final SocialPlayer _player;
	private final String _name;
	private int _size;
	private GUIMenu _previousMenu = null;
	private Inventory _inventory;
	
	/*Constructors*/
	public GUIMenu(SocialPlayer player, String name, int size) {
		_player = player;
		_name = name;
		_size = size;
	}
	
	/*Getters and setters*/
	public SocialPlayer getPlayer() {
		return _player;
	}
	
	public int getSize() {
		return _size;
	}

	protected void setSize(int size) {
		this._size = size;
	}

	public @Nullable GUIMenu getPreviousMenu() {
		return _previousMenu;
	}
	
	public void setPreviousMenu(@Nullable GUIMenu menu) {
		_previousMenu = menu;
	}
	
	public void addClickable(int slot, Clickable clickable) {
		_clickables.put(slot, clickable);
	}

	public void removeClickable(int slot) {
		_clickables.remove(slot);
	}
	
	public @Nullable Clickable getClickable(int slot) {
		return _clickables.get(slot);
	}
	
	public Inventory getInventory() {
		return _inventory;
	}

	/**
	 * Method that sub-classes should use in their constructors to fill the shop with clickables
	 */
	public abstract void fillShopWithClickables();
	
	/*Other methods*/

	public void resetClickables() {
		_clickables.clear();
	}

	private void placeClickables() {
		ItemStack item;

		for(int i : _clickables.keySet()) {
			item = _clickables.get(i).getItemStack();

			ItemMeta meta = item.getItemMeta();
			meta.getPersistentDataContainer().set(MineSocieties.getPlugin().getGuiManager().getGuiItemKey(), PersistentDataType.INTEGER, i);
			item.setItemMeta(meta);

			_inventory.setItem(i, item);
		}
	}

	/**
	 * Opens a new Inventory Menu containing all this shop's clickables in the player's view
	 */
	public void open() {
		_clickables = new HashMap<>();
		
		_inventory = Bukkit.createInventory(_player.getPlayer(), _size, _name);
		
		fillShopWithClickables();

		placeClickables();

		_player.getPlayer().openInventory(_inventory);
		_player.setCurrentOpenGUIMenu(this);
	}
	
	/**
	 * Called to refresh all the menu's clickable. Usually called when a ShopItem changes (type, name, lore) and its changes need to be updated
	 */
	public void update() {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				placeClickables();
			}
		}.runTaskLater(MineSocieties.getPlugin(), 0);
	}

	/**
	 *  Resets the menu, by removing all items from the inventory and filling it up with all
	 * existing clickable.
	 */
	public void reset() {
		new BukkitRunnable() {

			@Override
			public void run() {
				_inventory.clear();

				placeClickables();
			}
		}.runTaskLater(MineSocieties.getPlugin(), 0);
	}

	public void fillRestWithPanes(EmptyPaneDecoration pane) {
		for (int i = 0; i < getSize(); i++) {
			if (getClickable(i) == null) {
				addClickable(i, pane);
			}
		}
	}

	public void fillRestWithPanes() {
		fillRestWithPanes(new EmptyPaneDecoration(this));
	}

	public void fillRestWithPanes(Material paneMaterial) {
		fillRestWithPanes(new EmptyPaneDecoration(this, paneMaterial));
	}
}
