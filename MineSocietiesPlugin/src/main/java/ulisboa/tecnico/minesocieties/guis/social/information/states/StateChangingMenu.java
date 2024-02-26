package ulisboa.tecnico.minesocieties.guis.social.information.states;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitRunnable;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.CollectionOfStates;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.*;

import java.util.Arrays;

public abstract class StateChangingMenu<T extends CollectionOfStates<?>> extends PageableMenu {

    // Private attributes

    private final SocialAgent agent;
    private final T states;

    private static final int PAGE_SIZE = 45;

    // Constructors

    public StateChangingMenu(SocialPlayer player, SocialAgent agent, T states, String name) {
        super(player, name, 54);

        this.agent = agent;
        this.states = states;
    }

    @Override
    public void fillShopWithClickables() {
        reloadItemsInPage();
    }

    private void reloadItemsInPage() {
        // Button to add new entries
        addClickable(45, new StateAdder());
        addClickable(53, new GoBack(this));

        // Information regarding the menu
        addClickable(49, new InformativeItem(this));

        if (states.entrySizes() > PAGE_SIZE) {
            addBottomLayer();
        }

        fillPageFromList(PAGE_SIZE, states.getStates().stream().toList(), StateItem::new);
        fillRestWithPanes(Material.BLUE_STAINED_GLASS_PANE);

        update();
    }

    @Override
    public void moveToNextPage() {
        reloadItemsInPage();
    }

    @Override
    public void moveToPreviousPage() {
        reloadItemsInPage();
    }

    @Override
    public int getMaxPages() {
        return (states.entrySizes() / PAGE_SIZE) + 1;
    }

    // Private classes

    private class StateItem extends GUIItem {

        // Private attributes

        private final String state;

        // Constructors

        public StateItem(String state) {
            super(StateChangingMenu.this, Material.PAPER, ChatColor.BLUE + state);

            this.state = state;

            // New line
            addDescription("");

            addDescription(ChatColor.RED + "Right-click to delete");
        }

        // Other methods

        @Override
        public void clicked(ClickType click) {
            if (click.isRightClick()) {
                states.removeState(state);
                agent.getState().markDirty();

                getPlayer().getPlayer().playSound(getPlayer().getPlayer().getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);

                hardReset();
            }
        }
    }

    private class StateAdder extends GUIItem {

        // Constructors

        public StateAdder() {
            super(StateChangingMenu.this, Material.OAK_SIGN, ChatColor.GREEN + "Click to add a new state");
        }

        @Override
        public void clicked(ClickType click) {
            // Opening a Sign GUI to allow the player to write the new state
            MineSocieties.getPlugin().getGuiManager().openSignGUI(getPlayer(), lines -> {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Collecting all lines into a single String
                        String newState = lines.stream().reduce("", (a, b) -> a + b);

                        if (newState.isEmpty()) {
                            new ErrorMenu(getPlayer(), "You must write something!", StateChangingMenu.this).open();
                        } else if (!newState.matches("\\S+")) { // \S+ matches any non-whitespace character
                            // The new state contains more than 1 word
                            new ErrorMenu(getPlayer(), "Write a single word or hiphenated words!", StateChangingMenu.this).open();
                        } else {
                            // All is well. Adding the new state
                            states.addState(newState);
                            agent.getState().markDirty();

                            getPlayer().getPlayer().playSound(getPlayer().getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

                            StateChangingMenu.this.open();
                        }
                    }
                }.runTask(MineSocieties.getPlugin());
            });
        }
    }
}
