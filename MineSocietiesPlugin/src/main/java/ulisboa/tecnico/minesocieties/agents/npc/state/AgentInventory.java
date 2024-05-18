package ulisboa.tecnico.minesocieties.agents.npc.state;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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

    public Map<Integer, ItemStack> tryAddItems(ItemStack... items) {
        lock.writeLock();
        Map<Integer, ItemStack> leftover = addItems(items);
        lock.writeUnlock();

        return leftover;
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

    public boolean hasItems() {
        lock.readLock();

        // Trying to find a non-null item
        for (ItemStack item : inventory.getContents()) {
            if (item != null) {
                lock.readUnlock();

                return true;
            }
        }

        lock.readUnlock();

        // Found only null items
        return false;
    }

    public boolean hasItem(Material material) {
        lock.readLock();

        // Trying to find an item with the given material
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == material) {
                lock.readUnlock();

                return true;
            }
        }

        lock.readUnlock();

        // Found no item with the given material
        return false;
    }

    public boolean hasAndRemoveItem(Material item, int amount) {
        if (countAmountOfItem(item) < amount) {
            return false;
        }

        lock.writeLock();

        if (countAmounfOfItemAux(item) < amount) {
            // The amount was modified in the meantime
            lock.writeUnlock();

            return false;
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);

            if (itemStack != null && itemStack.getType() == item) {
                if (amount == 0) {
                    // There's no more items to remove
                    lock.writeUnlock();

                    return true;
                }

                if (itemStack.getAmount() > amount) {
                    // Found an item stack with more than enough
                    itemStack.setAmount(itemStack.getAmount() - amount);

                    lock.writeUnlock();

                    return true;
                } else {
                    // Found a small item stack
                    amount -= itemStack.getAmount();

                    inventory.setItem(i, null);
                }
            }
        }

        // This point shouldn't be reached since the count assured there were enough items to be removed
        lock.writeUnlock();

        return true;
    }

    public void removeItem(ItemStack item) {
        // TODO: If the item has different NBT tags, it will not be removed (ex: damaged Iron Helmet)
        // Probably have to do my own remove method that only checks the type of the item

        if (!lock.tryWrite(() -> inventory.removeItemAnySlot(item))) {
            // Couldn't remove the item, try again in the next tick
            new BukkitRunnable() {
                @Override
                public void run() {
                    removeItem(item);
                }
            }.runTask(MineSocieties.getPlugin());
        }
    }

    private int countAmounfOfItemAux(Material item) {
        int count = 0;

        for (ItemStack inventoryItem : inventory.getContents()) {
            if (inventoryItem != null && inventoryItem.getType() == item) {
                count += inventoryItem.getAmount();
            }
        }

        return count;
    }

    public int countAmountOfItem(ItemStack item) {
        return countAmountOfItem(item.getType());
    }

    public int countAmountOfItem(Material item) {
        lock.readLock();

        int count = countAmounfOfItemAux(item);

        lock.readUnlock();

        return count;
    }

    @Override
    public String accept(IContextVisitor visitor) {
        return visitor.explainInventory(this);
    }
}