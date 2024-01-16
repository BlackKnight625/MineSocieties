package ulisboa.tecnico.minesocieties.guis;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.bukkit.event.inventory.ClickType.NUMBER_KEY;

public class GuiManager {

    // Private attributes

    private final NamespacedKey guiItemKey;

    private static final String GUI_ITEM_KEY = "gui_item";

    // Constructors

    public GuiManager(Plugin plugin) {
        guiItemKey = new NamespacedKey(plugin, GUI_ITEM_KEY);
    }

    // Getters and setters

    public NamespacedKey getGuiItemKey() {
        return guiItemKey;
    }

    // Other methods

    /**
     *  Called every time a click takes placed inside an inventory
     * @param player
     *  The player that clicked
     * @param action
     *  The clicking action
     * @param currentItem
     *  The item that was clicked
     * @param cursorItem
     *  The item on the player's cursor
     * @param click
     *  The type of click that the player did
     * @return
     *  True if the clicking should be cancelled. False otherwise
     */
    public boolean clickedInventory(SocialPlayer player, InventoryAction action, ItemStack currentItem, ItemStack cursorItem, ClickType click, Integer index) {
        if (click == NUMBER_KEY) {
            var itemStack = player.getPlayer().getInventory().getItem(index);
            if (itemStack != null && itemStack.getItemMeta()!=null) {
                if (itemStack.getItemMeta().getPersistentDataContainer().get(guiItemKey, PersistentDataType.INTEGER) != null) {
                    return true;
                }
            }
        }
        if (currentItem != null && currentItem.getItemMeta() != null) {
            // Dealing with GUI items
            if (player != null && player.getCurrentOpenGUIMenu() != null && action != InventoryAction.NOTHING
                    && currentItem.getItemMeta().getPersistentDataContainer().has(guiItemKey, PersistentDataType.INTEGER)) {

                // The player clicked on a GUI item

                if (cursorItem == null || cursorItem.getType() == Material.AIR) {
                    // The player isn't holding an item on their cursor

                    //Telling the item in its current Menu that it has been clicked
                    var namespcaedkey = currentItem.getItemMeta().getPersistentDataContainer().get(guiItemKey, PersistentDataType.INTEGER);
                    assert namespcaedkey != null;

                    var clickable =  player.getCurrentOpenGUIMenu().getClickable(namespcaedkey);

                    assert clickable != null;
                    clickable.click(click);
                }

                return true;
            }
        }

        return false;
    }

    public void openSignGUI(SocialPlayer player, Consumer<List<String>> callback) {
        player.setSignEditAction(callback);

        MineSocieties.getPlugin().getPacketManager().openSignEditor(player);
    }

    public void signChanged(SocialPlayer player, String[] lines) {
        if(player.hasSignEditAction()) {
            player.getSignEditAction().accept(Arrays.asList(lines));
            player.setSignEditAction(null);
        }
    }
}
