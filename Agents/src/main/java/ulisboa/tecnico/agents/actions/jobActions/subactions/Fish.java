package ulisboa.tecnico.agents.actions.jobActions.subactions;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.LootTables;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.entityutils.entity.npc.EntityAnimation;
import ulisboa.tecnico.agents.actions.ActionStatus;
import ulisboa.tecnico.agents.actions.TemporalAction;
import ulisboa.tecnico.agents.npc.IAgent;
import ulisboa.tecnico.agents.utils.DisplayUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Fish<T extends IAgent> extends TemporalAction<T> {

    // Private attributes

    private final int maxTicksPerFish;
    private final Block waterBlock;
    private FishHook fishHook;
    private boolean startedBobbing;
    private boolean fishHasBitten = false;
    private final Plugin plugin;

    private static final LootTable FISHING_LOOT_TABLE = LootTables.FISHING.getLootTable();
    private static final int MAX_WAIT_FOR_START_BOBBING_TICKS = 40;
    private static final Map<UUID, Fish<? extends IAgent>> PLAYER_TO_FISH_ACTION = new HashMap<>();
    private static final Random RANDOM = new Random();

    // Constructors

    public Fish(int maxTicksPerFish, Block waterBlock, Plugin plugin) {
        this.maxTicksPerFish = maxTicksPerFish;
        this.waterBlock = waterBlock;
        this.plugin = plugin;
    }

    // Other methods

    @Override
    public void start(T actioner) {
        ItemStack fishingRod = new ItemStack(Material.FISHING_ROD);

        actioner.getAgent().setItem(fishingRod, EquipmentSlot.HAND);

        Vector castVelocity = waterBlock.getLocation().add(0.5, 1, 0.5).toVector().subtract(actioner.getEyeLocation().toVector());

        System.out.println("Cast velocity: " + castVelocity);

        castVelocity.normalize().multiply(2);

        actioner.getAgent().lookAt(castVelocity);

        fishHook = actioner.getAgent().castFishingHook(castVelocity);

        new BukkitRunnable() {
            @Override
            public void run() {
                fishHook.setVelocity(castVelocity);
                System.out.println("Velocity: " + fishHook.getVelocity());
            }
        }.runTaskLater(plugin, 1);

        // Temporary
        fishHook.setWaitTime(40, 100);

        // Registering this action to be notified when a player fishes
        PLAYER_TO_FISH_ACTION.put(actioner.getUUID(), this);
    }

    @Override
    public ActionStatus tick(T actioner, int elapsedTicks) {
        if (startedBobbing) {
            // The hook has reached water
            if (fishHasBitten) {
                // A fish has been caught
                actioner.getAgent().animate(EntityAnimation.SWING_MAIN_ARM);

                // Getting the loot, with an artificial CraftPlayer holding the agent
                var items = FISHING_LOOT_TABLE.populateLoot(
                        RANDOM,
                        new LootContext.Builder(actioner.getLocation())
                                .killer(new CraftPlayer((CraftServer) Bukkit.getServer(), actioner.getNPCData().getNpc()))
                                .build()
                );

                actioner.acquiredFishLoot(items);

                // Showing the earned loot on an ItemDisplay
                Location location = fishHook.getLocation();
                double offset = 1;

                for (ItemStack item : items) {
                    ItemDisplay itemDisplay = DisplayUtils.createTemporaryItemDisplay(item, location.clone().add(0, offset, 0), 40, plugin);

                    itemDisplay.setGlowing(true);
                    itemDisplay.setGlowColorOverride(Color.GREEN);

                    offset += 0.5;
                }

                clear(actioner);

                return ActionStatus.SUCCESS;
            }

            // Checking if too much time has passed
            if (elapsedTicks > maxTicksPerFish) {
                clear(actioner);

                return ActionStatus.FAILURE;
            }
        } else {
            // The hook hasn't reached water yet

            // Checking if the hook has reached water
            startedBobbing = fishHook.getState() == FishHook.HookState.BOBBING;

            // Checking if too much time has passed
            if (!startedBobbing && elapsedTicks > MAX_WAIT_FOR_START_BOBBING_TICKS) {
                clear(actioner);

                return ActionStatus.FAILURE;
            }
        }

        return ActionStatus.IN_PROGRESS;
    }

    @Override
    public void cancel(T actioner) {
        clear(actioner);
    }

    public void clear(T actioner) {
        fishHook.remove();

        actioner.getAgent().setItem(null, EquipmentSlot.HAND);

        PLAYER_TO_FISH_ACTION.remove(actioner.getUUID());
    }

    public void fishEvent(PlayerFishEvent e) {
        if (e.getState() == PlayerFishEvent.State.BITE) {
            fishHasBitten = true;
        }
    }

    public static void notifyPlayerFished(UUID playerUuid, PlayerFishEvent e) {
        Fish<? extends IAgent> fishAction = PLAYER_TO_FISH_ACTION.get(playerUuid);

        if (fishAction != null) {
            fishAction.fishEvent(e);
        }
    }
}
