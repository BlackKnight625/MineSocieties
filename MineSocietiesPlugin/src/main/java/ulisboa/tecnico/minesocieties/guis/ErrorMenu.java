package ulisboa.tecnico.minesocieties.guis;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ErrorMenu extends GUIMenu {

    // Private attributes

    private final GUIMenu goBackTo;
    private final ItemStack itemWithErrorDescription;

    private static final int MAX_LETTERS_PER_PARAGRAPH = 35;

    // Constructors

    public ErrorMenu(SocialPlayer player, String errorMessage, @Nullable GUIMenu goBackTo) {
        super(player, "ERROR!", 27);

        this.goBackTo = goBackTo;

        // Creating lore to the menu's items that show what error took place
        String[] words = errorMessage.split(" ");
        List<String> description = new LinkedList<>();
        StringBuilder line = new StringBuilder();
        int currentLetters = 0;

        for (String word : words) {
            if (currentLetters < MAX_LETTERS_PER_PARAGRAPH) {
                // Adding 1 more word to the paragraph
                line.append(word).append(' ');
                currentLetters += word.length();
            } else {
                // Adding a new line
                description.add(ChatColor.RED + line.toString());
                line = new StringBuilder();
                line.append(word).append(' ');
                currentLetters = word.length();
            }
        }

        if(!line.isEmpty()) {
            description.add(ChatColor.RED + line.toString());
        }

        description.add(ChatColor.AQUA + "Click me to go back.");

        // Creating the item
        itemWithErrorDescription = new ItemStack(Material.BARRIER);
        ItemMeta meta = itemWithErrorDescription.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "Error!");
        meta.setLore(description);
        itemWithErrorDescription.setItemMeta(meta);
    }

    private ErrorMenu(SocialPlayer player, GUIException error, @Nullable GUIMenu goBackTo) {
        this(player, error.getMessage(), goBackTo);
    }

    // Other methods

    @Override
    public void fillShopWithClickables() {
        for(int i = 0; i < 27; i++) {
            addClickable(i, new GoBack());
        }
    }

    /**
     *  Called when there's an error while interacting with GUIs.
     *  This will open up a new GUI detailing the error.
     *  The error can either be induced by the user, GUIException, (such as: "You are not allowed to click this!"),
     * or a server-side error
     *  Note: This method should be called in the main Minecraft thread
     * @param player
     *  The player interacting with the GUI
     * @param goBackTo
     *  The menu to go back to when the player clicks on the error items, which work
     * similarly to the GoBack item.
     *  If null, then the error menu gets closed.
     * @param guiMessage
     *  The message to be displayed to the player on the GUI if the exception is not a GUIException
     * @param e
     *  The error that took place
     */
    public static void error(SocialPlayer player, @Nullable GUIMenu goBackTo, Throwable e, String guiMessage) {
        if(e instanceof GUIException guiException) {
            // A user-induced error was caught
            new ErrorMenu(player, guiException, goBackTo).open();
        }
        else {
            // A server-side error was caught
            GUIException error = new GUIException(guiMessage + " Please report this" +
                    " error. Timestamp: " + new Date() + ". Server: " + MineSocieties.getPlugin().getServer().getName() +
                    ". Details: " + e.getMessage());

            new ErrorMenu(player, error, goBackTo).open();

            e.printStackTrace();
        }
    }

    public static void error(SocialPlayer player, @Nullable GUIMenu goBackTo, GUIException e) {
        error(player, goBackTo, e, "");
    }

    private class GoBack extends GUIItem {

        public GoBack() {
            super(ErrorMenu.this, itemWithErrorDescription);
        }

        @Override
        public void clicked(ClickType click) {
            if(goBackTo != null) {
                goBackTo.open();
            }
            else {
                getPlayer().getPlayer().closeInventory();
            }
        }
    }
}
