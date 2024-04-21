package ulisboa.tecnico.minesocieties.agents.observation;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import ulisboa.tecnico.agents.observation.EventListener;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.SocialAgentManager;
import ulisboa.tecnico.minesocieties.agents.SocialCharacter;
import ulisboa.tecnico.minesocieties.agents.npc.MessageDisplay;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentInventory;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentState;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.GuiManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SocialEventListener extends EventListener {

    // Private attributes

    private final NamespacedKey droppedByKey;

    // Constructors

    public SocialEventListener(SocialAgentManager manager) {
        super(manager);

        droppedByKey = new NamespacedKey(manager.getPlugin(), "dropped_by");
    }

    // Getters and setters

    @Override
    public SocialAgentManager getManager() {
        return (SocialAgentManager) super.getManager();
    }

    // Event listeners

    @EventHandler
    public void onEntitiesLoad(EntitiesLoadEvent e) {
        for (Entity entity : e.getEntities()) {
            UUID socialPlayerUuid = SocialAgent.getUuidFromContainer(entity.getPersistentDataContainer());

            if (socialPlayerUuid != null) {
                // This entity belongs to a Social NPC
                SocialAgent socialAgent = getManager().getAgent(socialPlayerUuid);

                if (socialAgent == null) {
                    // This agent doesn't exist
                    if (entity instanceof TextDisplay) {
                        // This used to be a part of an agent's Message Display
                        entity.remove();
                    }
                } else {
                    if (entity instanceof TextDisplay textDisplay) {
                        // This is an agent's Message Display
                        MessageDisplay currentDisplay = socialAgent.getMessageDisplay();

                        if (currentDisplay.getTextDisplay().isValid()) {
                            // The agent's current display is still valid. Therefore, this display is outdated and should
                            // be removed
                            entity.remove();
                        } else {
                            // The agent does not have a valid display, so this display should be set as the agent's
                            socialAgent.getMessageDisplay().setTextDisplay(textDisplay);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                getSocialAgentManager().addPlayerAsViewer(e.getPlayer());
            }
        }.runTask(MineSocieties.getPlugin());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                getSocialAgentManager().removePlayerAsViewer(e.getPlayer());
            }
        }.runTask(MineSocieties.getPlugin());

        SocialPlayer player = toSocialPlayer(e.getPlayer());

        player.noLongerEdittingCustomMenus();
    }

    @EventHandler
    public void playerMoves(PlayerMoveEvent e) {
        SocialPlayer player = toSocialPlayer(e.getPlayer());

        // Player moved. Cancelling the listening for this player's editing menus
        player.noLongerEdittingCustomMenus();
    }

    @EventHandler
    public void playerClicksInventory(InventoryClickEvent e) {
        e.setCancelled(MineSocieties.getPlugin().getGuiManager()
                .clickedInventory(
                        getSocialAgentManager().getPlayerWrapper((Player) e.getWhoClicked()),
                        e.getAction(),
                        e.getCurrentItem(),
                        e.getCursor(),
                        e.getClick(),
                        e.getHotbarButton()
                ));
    }

    @EventHandler
    public void playerClosesInventory(InventoryCloseEvent e) {
        if (e.getPlayer() instanceof Player player) {
            SocialPlayer socialPlayer = toSocialPlayer(player);

            MineSocieties.getPlugin().getGuiManager().closedInventory(socialPlayer);
        }
    }

    @EventHandler
    public void playerInteractsWithEntity(PlayerInteractEntityEvent e) {
        ItemStack itemInHand = e.getPlayer().getEquipment().getItem(e.getHand());

        if (MineSocieties.getPlugin().getGuiManager().isNPCStick(itemInHand) && e.getRightClicked() instanceof Player otherPlayer) {
            SocialAgent agent = MineSocieties.getPlugin().getSocialAgentManager().getAgent(otherPlayer.getUniqueId());

            if (agent != null) {
                // Player right-clicked on an NPC with an NPC Edit Stick
                MineSocieties.getPlugin().getGuiManager().openAgentMenu(toSocialPlayer(e.getPlayer()), agent);
            }
        }
    }

    @EventHandler
    public void playerInteracts(PlayerInteractEvent e) {
        MineSocieties.getPlugin().getGuiManager().playerInteracted(e);
    }

    // Other methods

    private SocialAgentManager getSocialAgentManager() {
        return MineSocieties.getPlugin().getSocialAgentManager();
    }

    public SocialPlayer toSocialPlayer(Player player) {
        return getSocialAgentManager().getPlayerWrapper(player);
    }

    @Override
    public void register() {
        super.register();

        // Creating some periodic tasks that may generate observations

        // Checking if agents are near item entities to pick them up
        new BukkitRunnable() {
            @Override
            public void run() {
                MineSocieties.getPlugin().getSocialAgentManager().forEachValidAgent(agent -> {
                    AgentState state = agent.getState();
                    AgentInventory inventory = state.getInventory();

                    for (Item item : agent.getLocation().getNearbyEntitiesByType(Item.class, 1)) {
                        if (item.getPickupDelay() != 0) {
                            // The item is not ready to be picked up
                            continue;
                        }

                        SocialCharacter owner = getOwnerOfDroppedItem(item);

                        ItemStack pickedUpStack = null;

                        // Trying to add this item to the agent's inventory
                        var leftover = inventory.tryAddItems(item.getItemStack());

                        if (leftover.isEmpty()) {
                            pickedUpStack = item.getItemStack();

                            item.remove();
                        } else {
                            ItemStack leftoverItemStack = leftover.values().iterator().next();
                            ItemStack currentItemStack = item.getItemStack();

                            if (!leftoverItemStack.equals(currentItemStack)) {
                                // Only part of the item was picked up
                                item.setItemStack(leftoverItemStack);

                                pickedUpStack = currentItemStack.clone();
                                pickedUpStack.setAmount(currentItemStack.getAmount() - leftoverItemStack.getAmount());
                            }
                        }

                        if (pickedUpStack != null) {
                            // The agent picked up at least something
                            state.markDirty();

                            // Playing a sound
                            item.getWorld().playSound(item.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);

                            ItemPickupObservation observation = new ItemPickupObservation(pickedUpStack, item,
                                    owner == null ? null : owner.toReference());

                            observation.accept(agent);
                        }
                    }
                });
            }
        }.runTaskTimer(MineSocieties.getPlugin(), 10, 10);
    }

    public void characterDroppedItem(Item item, SocialCharacter character) {
        associateDroppedItemWithOwner(item, character);
    }

    public void associateDroppedItemWithOwner(Item item, SocialCharacter owner) {
        // Associating the dropped item with the owner
        item.getPersistentDataContainer().set(droppedByKey, PersistentDataType.STRING, owner.getUUID().toString());
    }

    public @Nullable SocialCharacter getOwnerOfDroppedItem(Item item) {
        if (item.getThrower() != null && Bukkit.getEntity(item.getThrower()) instanceof Player) {
            // The item was thrown by a player
            return getSocialAgentManager().getCharacter(item.getThrower());
        }

        // Checking if the item was thrown by an agent
        String ownerUuid = item.getPersistentDataContainer().get(droppedByKey, PersistentDataType.STRING);

        if (ownerUuid == null) {
            return null;
        }

        return getSocialAgentManager().getCharacter(UUID.fromString(ownerUuid));
    }
}
