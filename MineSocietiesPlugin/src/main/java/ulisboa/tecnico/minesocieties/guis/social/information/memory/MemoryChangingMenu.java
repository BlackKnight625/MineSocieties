package ulisboa.tecnico.minesocieties.guis.social.information.memory;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitRunnable;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.InstantMemory;
import ulisboa.tecnico.minesocieties.agents.npc.state.TemporaryMemory;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.GUIItem;
import ulisboa.tecnico.minesocieties.guis.common.GoBack;
import ulisboa.tecnico.minesocieties.guis.common.PageableMenu;
import ulisboa.tecnico.minesocieties.utils.StringUtils;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public abstract class MemoryChangingMenu<T extends InstantMemory> extends PageableMenu {

    // Private attributes

    private final SocialAgent agent;
    private final TemporaryMemory<T> memory;

    private static final int PAGE_SIZE = 45;

    // Constructors

    public MemoryChangingMenu(SocialPlayer player, SocialAgent agent, TemporaryMemory<T> memory, String name) {
        super(player, name, 54);

        this.agent = agent;
        this.memory = memory;
    }

    // Other methods


    @Override
    public void fillShopWithClickables() {
        reloadItemsInPage();
    }

    private void reloadItemsInPage() {
        // Button to add new entries
        addClickable(45, new SectionAdder());
        addClickable(53, new GoBack(this));

        if(memory.entrySizes() > PAGE_SIZE) {
            addBottomLayer();
        }

        fillPageFromList(PAGE_SIZE, memory.getMemorySections().stream().toList(), SectionItem::new);
        fillRestWithPanes(Material.WHITE_STAINED_GLASS_PANE);

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
        return (memory.entrySizes() / PAGE_SIZE) + 1;
    }

    protected String getInstant(T section) {
        return DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm:ss").format(section.getInstant().atOffset(ZoneOffset.UTC));
    }

    /**
     *  Returns an explained version of the memory section, to be displayed in this menu as a single item
     * @param section
     *  The section that must be converted to a String
     * @return
     *  An explanation of the section
     */
    protected abstract String getExplainedSection(T section);

    protected abstract T newSectionFromLine(String line);

    // Private classes

    private class SectionItem extends GUIItem {

        // Private attributes

        private final T section;

        // Constructors

        public SectionItem(T section) {
            super(MemoryChangingMenu.this, Material.PAPER, "");

            this.section = section;

            // Setting the item's name with the date
            getItemStack().editMeta(meta -> meta.displayName(Component.text(getInstant(section) + " UTC").color(TextColor.color(62, 229, 229))));

            // Setting the item's lore with the memory
            addDescription(ChatColor.GRAY, StringUtils.splitIntoLines(getExplainedSection(section), 30));

            // New line
            addDescription("");

            addDescription(ChatColor.RED + "Right-click to delete");
        }

        // Other methods

        @Override
        public void clicked(ClickType click) {
            if (!click.isLeftClick()) {
                memory.remove(section);
                agent.getState().saveAsync();

                getPlayer().getPlayer().playSound(getPlayer().getPlayer().getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);

                reset();
            }
        }
    }

    private class SectionAdder extends GUIItem {

            // Constructors

            public SectionAdder() {
                super(MemoryChangingMenu.this, Material.WRITABLE_BOOK, ChatColor.GREEN + "Click to add a new section");
            }

            // Other methods

            @Override
            public void clicked(ClickType click) {
                if (click.isLeftClick()) {
                    // Closing this menu so the player may type in the book
                    getPlayer().getPlayer().getOpenInventory().close();

                    // Giving the player a writable book so they may add a new section
                    MineSocieties.getPlugin().getGuiManager().giveCustomEditingBook(getPlayer(), lines -> {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (lines.isEmpty()) {
                                    getPlayer().getPlayer().sendMessage(
                                            Component.text("You didn't write anything!").color(TextColor.color(255, 0, 0))
                                    );
                                } else if (lines.size() > 1) {
                                    getPlayer().getPlayer().sendMessage(
                                            Component.text("You wrote more than one line! Only the 1st shall be considered.").color(TextColor.color(255, 0, 0))
                                    );

                                    getPlayer().getPlayer().playSound(getPlayer().getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                                } else {
                                    T newSection = newSectionFromLine(lines.get(0));

                                    memory.addMemorySection(newSection);
                                    agent.getState().saveAsync();

                                    getPlayer().getPlayer().playSound(getPlayer().getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                                }

                                // Re-opening the menu since the book forces the player to close it
                                MemoryChangingMenu.this.open();
                            }
                        }.runTask(MineSocieties.getPlugin());
                    }, "Write a single line to add to the memory");
                }
            }
    }
}
