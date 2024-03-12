package ulisboa.tecnico.agents.utils;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DisplayUtils {

    // Private attributes

    private static final NamespacedKey TEMPORARY_DISPLAY_KEY = new NamespacedKey("agents_temporary", "agents_temporary");

    // Other methods

    public static ItemDisplay createTemporaryItemDisplay(ItemStack item, Location location, int ticks, Plugin plugin) {
        ItemDisplay itemDisplay = location.getWorld().spawn(location.clone().setDirection(new Vector(1, 0, 0)), ItemDisplay.class);

        itemDisplay.setItemStack(item);
        itemDisplay.setBillboard(Display.Billboard.CENTER);

        itemDisplay.getPersistentDataContainer().set(TEMPORARY_DISPLAY_KEY, PersistentDataType.BOOLEAN, true);

        // Removing the entity after a while
        new BukkitRunnable() {
            @Override
            public void run() {
                itemDisplay.remove();
            }
        }.runTaskLater(plugin, ticks);

        return itemDisplay;
    }

    public static boolean isTemporaryDisplay(Display displayEntity) {
        Boolean isTemporary = displayEntity.getPersistentDataContainer().get(TEMPORARY_DISPLAY_KEY, PersistentDataType.BOOLEAN);

        return isTemporary != null && isTemporary;
    }
}
