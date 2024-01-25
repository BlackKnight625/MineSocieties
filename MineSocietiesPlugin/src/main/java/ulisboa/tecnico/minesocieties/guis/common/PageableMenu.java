package ulisboa.tecnico.minesocieties.guis.common;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;

import java.util.List;
import java.util.function.Function;

public abstract class PageableMenu extends GUIMenu {

    // Private attributes

    private int page = 0;

    // Constructors

    public PageableMenu(SocialPlayer player, String name, int size) {
        super(player, name, size);
    }

    // Getters and setters

    public int getPage() {
        return page;
    }

    // Other methods

    /**
     *  Creates clickables for the last menu row with buttons to go back, move to the
     * next page and move to the previous page
     */
    public void addBottomLayer() {
        int slot;

        if(getClickable((slot = getSize() - 1)) == null) addClickable(slot, new GoBack(this));
        if(getClickable((slot = getSize() - 6)) == null) addClickable(slot, new PreviousPage(this));
        if(getClickable((slot = getSize() - 5)) == null) addClickable(slot, new EmptyPaneDecoration(this));
        if(getClickable((slot = getSize() - 4)) == null) addClickable(slot, new NextPage(this));

        for(int i = getSize() - 8; i < getSize() - 6; i++) {
            if(getClickable(i) == null) addClickable(i, new EmptyPaneDecoration(this));
        }

        for(int i = getSize() - 3; i < getSize(); i++) {
            if(getClickable(i) == null) addClickable(i, new EmptyPaneDecoration(this));
        }
    }

    public <T> void fillPageFromList(int firstSlot, int maxPerPage, List<T> items, Function<T, GUIItem> toGUIItem) {
        int min = getPage() * maxPerPage;
        int max = Math.min((getPage() + 1) * maxPerPage, items.size());

        // Removing the previous items
        for(int i = 0; i < maxPerPage; i++) {
            removeClickable(i + firstSlot);
            getInventory().setItem(i + firstSlot, new ItemStack(Material.AIR));
        }

        // Adding the items
        for(int i = min; i < max; i++) {
            addClickable((i % maxPerPage)  + firstSlot, toGUIItem.apply(items.get(i)));
        }
    }

    public <T> void fillPageFromList(int maxPerPage, List<T> items, Function<T, GUIItem> toGUIItem) {
        fillPageFromList(0, maxPerPage, items, toGUIItem);
    }

    public void nextPage() {
        if(page + 1 < getMaxPages()) {
            page++;
            moveToNextPage();
        }
    }

    public void previousPage() {
        if(page - 1 >= 0) {
            page--;
            moveToPreviousPage();
        }
    }

    public abstract void moveToNextPage();

    public abstract void moveToPreviousPage();

    public abstract int getMaxPages();
}
