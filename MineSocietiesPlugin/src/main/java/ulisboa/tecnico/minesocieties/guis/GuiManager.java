package ulisboa.tecnico.minesocieties.guis;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.SocialAgentManager;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.social.SocialAgentMainMenu;
import ulisboa.tecnico.minesocieties.utils.ComponentUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.bukkit.event.inventory.ClickType.NUMBER_KEY;

public class GuiManager {

    // Private attributes

    private final NamespacedKey guiItemKey;
    private final NamespacedKey customBookKey;
    private final NamespacedKey npcEditStickKey;
    private final Map<Integer, Consumer<List<String>>> customBookActions = new HashMap<>();
    private int lastCustomBookId = 0;

    private static final String GUI_ITEM_KEY = "gui_item";
    private static final String CUSTOM_BOOK_KEY = "custom_book";
    private static final String NPC_EDIT_STICK_KEY = "npc_edit";

    // Constructors

    public GuiManager(Plugin plugin) {
        guiItemKey = new NamespacedKey(plugin, GUI_ITEM_KEY);
        customBookKey = new NamespacedKey(plugin, CUSTOM_BOOK_KEY);
        npcEditStickKey = new NamespacedKey(plugin, NPC_EDIT_STICK_KEY);
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

    public void giveCustomEditingBook(SocialPlayer player, Consumer<List<String>> callback, String title) {
        ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
        var bookMeta = (BookMeta) book.getItemMeta();

        bookMeta.displayName(Component.text("Custom Book: " + title).color(TextColor.color(255, 219, 49)));
        bookMeta.setAuthor("Server");
        bookMeta.setTitle(title);

        // Associating the action  to be performed when the book is edited
        int customBookId = lastCustomBookId++;

        customBookActions.put(customBookId, callback);
        bookMeta.getPersistentDataContainer().set(customBookKey, PersistentDataType.INTEGER, customBookId);

        // Cleaning the map after a while since the player may have dropped the book
        new BukkitRunnable() {
            @Override
            public void run() {
                if (customBookActions.containsKey(customBookId)) {
                    customBookActions.remove(customBookId);

                    // Telling the player that the edit action has been cancelled
                    player.getPlayer().sendMessage(
                            Component.text("You are no longer editing the book '" + title + "'.")
                                    .color(TextColor.color(255, 0, 0))
                    );
                }
            }
        }.runTaskLater(MineSocieties.getPlugin(), 20 * 60 * 5);

        book.setItemMeta(bookMeta);

        player.getPlayer().getInventory().addItem(book);

        // Sending a message to the player informing about the special book
        player.getPlayer().sendMessage(
                ComponentUtils.withPrefix(Component.text("You received the book '" + title + "'. Open it, make your changes, and then save it.")
                        .color(TextColor.color(123, 255, 86)))
        );
    }

    public void giveNPCStick(SocialPlayer player) {
        ItemStack npcEditStick = new ItemStack(Material.STICK);
        var itemMeta = npcEditStick.getItemMeta();

        itemMeta.displayName(Component.text("NPC Edit Stick").color(TextColor.color(255, 219, 49)));
        itemMeta.getPersistentDataContainer().set(npcEditStickKey, PersistentDataType.BOOLEAN, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);

        npcEditStick.setItemMeta(itemMeta);

        player.getPlayer().getInventory().addItem(npcEditStick);
    }

    public boolean isNPCStick(ItemStack item) {
        if (item != null && item.getItemMeta() != null) {
            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

            return container.has(npcEditStickKey, PersistentDataType.BOOLEAN);
        }

        return false;
    }

    public void giveNPCStickIfNotPresent(SocialPlayer player) {
        PlayerInventory playerInventory = player.getPlayer().getInventory();

        for (int i = 0; i < playerInventory.getSize(); i++) {
            ItemStack itemStack = playerInventory.getItem(i);

            if (isNPCStick(itemStack)) {
                return;
            }
        }

        giveNPCStick(player);
    }

    public void openAgentMenu(SocialPlayer player, SocialAgent agent) {
        new SocialAgentMainMenu(player, agent).open();
    }

    public void bookChanged(SocialPlayer player, ItemStack book, List<String> pages) {
        // Checking if the book is a custom one
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        PersistentDataContainer dataContainer = bookMeta.getPersistentDataContainer();

        if (dataContainer.has(customBookKey, PersistentDataType.INTEGER)) {
            // The book is a custom one

            int customBookId = dataContainer.get(customBookKey, PersistentDataType.INTEGER);

            if (customBookActions.containsKey(customBookId)) {
                // The book has an associated action
                List<String> lines = pages.stream().map(page -> page.split("\n")).flatMap(Arrays::stream).toList();

                try {
                    customBookActions.get(customBookId).accept(lines);
                } catch (Exception e) {
                    player.getPlayer().sendMessage(
                            Component.text("An error occurred while saving the book. Please check the console for more details. " + e.getMessage())
                                    .color(TextColor.color(255, 0, 0)));

                    e.printStackTrace();
                }
                customBookActions.remove(customBookId);
            } else {
                // The book doesn't have an associated action

                player.getPlayer().sendMessage(
                        Component.text("This custom book is no longer valid for editing.")
                                .color(TextColor.color(255, 0, 0))
                );
            }

            // Removing the book from the player's inventory
            PlayerInventory playerInventory = player.getPlayer().getInventory();

            for (int i = 0; i < playerInventory.getSize(); i++) {
                ItemStack itemStack = playerInventory.getItem(i);

                if (itemStack != null && itemStack.getItemMeta() != null) {
                    PersistentDataContainer otherContainer = itemStack.getItemMeta().getPersistentDataContainer();

                    if (otherContainer.has(customBookKey, PersistentDataType.INTEGER) &&
                            otherContainer.get(customBookKey, PersistentDataType.INTEGER) == customBookId) {
                        playerInventory.setItem(i, null);
                    }
                }
            }
        }
    }

    public void closedInventory(SocialPlayer player) {
        player.setCurrentOpenGUIMenu(null);
    }
}
