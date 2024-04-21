package ulisboa.tecnico.minesocieties.guis.social.inventory;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentInventory;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.GUIDecoration;
import ulisboa.tecnico.minesocieties.guis.common.GUIItem;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenu;
import ulisboa.tecnico.minesocieties.guis.common.GoBack;

public class AgentInventoryMenu extends GUIMenu {

    // Private attributes

    private final SocialAgent agent;
    private final boolean canEditInventory;

    // Constructors

    public AgentInventoryMenu(SocialPlayer player, SocialAgent agent, boolean canEditInventory) {
        super(player, agent.getName() + "'s inventory", AgentInventory.MAX_INVENTORY_SIZE + 9);

        this.agent = agent;
        this.canEditInventory = canEditInventory;
    }

    // Other methods

    @Override
    public void fillShopWithClickables() {
        AgentInventory agentInventory = agent.getState().getInventory();
        ItemStack[] contents = agentInventory.getInventory();

        // Showing the agent's inventory items
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];

            if (item == null) {
                addClickable(i, new EmptySlot());
            } else {
                addClickable(i, new GUIDecoration(this, item.clone()));
            }
        }

        addClickable(AgentInventory.MAX_INVENTORY_SIZE + 8, new GoBack(this));


        if (canEditInventory) {
            addClickable(AgentInventory.MAX_INVENTORY_SIZE + 4, new ActualInventoryOpener());
        }

        fillRestWithPanes(Material.LIME_STAINED_GLASS_PANE);
    }

    // Private classes

    private class EmptySlot extends GUIDecoration {

        // Constructors

        public EmptySlot() {
            super(AgentInventoryMenu.this, Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + "Empty slot");
        }
    }

    private class ActualInventoryOpener extends GUIItem {

        // Constructors

        public ActualInventoryOpener() {
            super(AgentInventoryMenu.this, Material.CHEST, ChatColor.GOLD + "Interact with their inventory");
        }

        // Other methods

        @Override
        public void clicked(ClickType click) {
            Inventory bukkitInventory = agent.getState().getInventory().getBukkitInventory();
            getPlayer().getPlayer().openInventory(bukkitInventory);

            //  While the player has the agent's inventory open, things may change and it's not easy to detect. As such,
            // the state will be marked dirty while the player has the inventory open

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (getPlayer().getPlayer().getOpenInventory().getTopInventory().equals(bukkitInventory)) {
                        agent.getState().markDirty();
                    }
                }
            }.runTaskTimer(MineSocieties.getPlugin(), 0, 5);
        }
    }
}
