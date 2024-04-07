package ulisboa.tecnico.minesocieties.agents.npc.state;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import ulisboa.tecnico.agents.utils.ReadWriteLock;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

import java.util.Map;

/**
 *  Represents an inventory of an agent.
 *  Writes should only occur in the main thread. Reads can be async.
 *  Access to the inventory uses a read-write lock with mostly tries to avoid blocking the main thread, since access
 * to agent's inventories can be frequent. Instead of blocking, the access to the inventory will be retried in the next
 * tick, should the lock be unavailable.
 */
public class AgentInventory implements IExplainableContext {

    // Private attributes

    private Inventory inventory = Bukkit.getServer().createInventory(null, MAX_INVENTORY_SIZE);
    private transient ReadWriteLock lock = new ReadWriteLock();

    public static final int MAX_INVENTORY_SIZE = 36;

    // Other methods

    public ItemStack[] getInventory() {
        lock.readLock();
        ItemStack[] items = inventory.getContents();
        lock.readUnlock();

        return items;
    }

    public Inventory getBukkitInventory() {
        return inventory;
    }

    /**
     *  Adds items to the inventory
     * @param items
     *  The items to be added
     * @return
     *  A map containing the items that could not be added
     */
    private Map<Integer, ItemStack> addItems(ItemStack... items) {
        return inventory.addItem(items);
    }

    public void addOrDropItem(SocialAgent agent, ItemStack... items) {
        // Trying to add or drop the items
        if (!lock.tryWrite(() -> {
            for (ItemStack leftover : addItems(items).values()) {
                agent.getLocation().getWorld().dropItemNaturally(agent.getLocation(), leftover);
            }
        })) {
            // Couldn't add the items, try again in the next tick
            new BukkitRunnable() {
                @Override
                public void run() {
                    addOrDropItem(agent, items);
                }
            }.runTask(MineSocieties.getPlugin());
        }
    }

    public void removeItemAt(int slot) {
        if (!lock.tryWrite(() -> inventory.setItem(slot, null))) {
            // Couldn't remove the item, try again in the next tick
            new BukkitRunnable() {
                @Override
                public void run() {
                    removeItemAt(slot);
                }
            }.runTask(MineSocieties.getPlugin());
        }
    }

    @Override
    public String accept(IContextVisitor visitor) {
        return visitor.explainInventory(this);
    }
}
