package ulisboa.tecnico.agents.utils;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

public class DisplayUtils {

    // Private attributes

    private static final NamespacedKey TEMPORARY_DISPLAY_KEY = new NamespacedKey("agents_temporary", "agents_temporary");

    // Other methods

    public static ItemDisplay createTemporaryItemDisplay(ItemStack item, Location location, int ticks, Plugin plugin, boolean bobUpAndDown) {
        ItemDisplay itemDisplay = location.getWorld().spawn(location.clone().setDirection(new Vector(1, 0, 0)), ItemDisplay.class);

        itemDisplay.setItemStack(item);
        itemDisplay.setBillboard(Display.Billboard.CENTER);
        itemDisplay.setInterpolationDuration(1);
        itemDisplay.setInterpolationDelay(1);

        itemDisplay.getPersistentDataContainer().set(TEMPORARY_DISPLAY_KEY, PersistentDataType.BOOLEAN, true);

        // Removing the entity after a while
        new BukkitRunnable() {

            @Override
            public void run() {
                itemDisplay.remove();
            }
        }.runTaskLater(plugin, ticks);

        if (bobUpAndDown) {
            new BukkitRunnable() {
                int elapsedTicks = 0;
                @Override
                public void run() {
                    if (elapsedTicks >= ticks) {
                        cancel();
                        return;
                    }
                    Transformation transformation = itemDisplay.getTransformation();
                    Vector3f translation = transformation.getTranslation();

                    translation.set(translation.x, (float) (Math.sin(elapsedTicks * Math.PI / 10) * 0.5), translation.z);

                    itemDisplay.setTransformation(transformation);

                    elapsedTicks++;
                }
            }.runTaskTimer(plugin, 0, 1);
        }

        return itemDisplay;
    }

    public static boolean isTemporaryDisplay(Display displayEntity) {
        Boolean isTemporary = displayEntity.getPersistentDataContainer().get(TEMPORARY_DISPLAY_KEY, PersistentDataType.BOOLEAN);

        return isTemporary != null && isTemporary;
    }
}
