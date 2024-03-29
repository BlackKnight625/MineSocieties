package ulisboa.tecnico.minesocieties.agents.npc.state;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;

import java.util.Map;

/**
 *  Represents an inventory of an agent.
 *  This should only be accessed in the main thread.
 */
public class AgentInventory {

    // Private attributes

    private Inventory inventory = Bukkit.getServer().createInventory(null, MAX_INVENTORY_SIZE);

    public static final int MAX_INVENTORY_SIZE = 36;

    // Other methods

    public ItemStack[] getInventory() {
        return inventory.getContents();
    }

    /**
     *  Adds items to the inventory
     * @param items
     *  The items to be added
     * @return
     *  A map containing the items that could not be added
     */
    public Map<Integer, ItemStack> addItems(ItemStack... items) {
        return inventory.addItem(items);
    }

    public void addOrDropItem(SocialAgent agent, ItemStack... items) {
        for (ItemStack leftover : addItems(items).values()) {
            agent.getLocation().getWorld().dropItemNaturally(agent.getLocation(), leftover);
        }
    }

    public void removeItemAt(int slot) {
        inventory.setItem(slot, null);
    }
}
