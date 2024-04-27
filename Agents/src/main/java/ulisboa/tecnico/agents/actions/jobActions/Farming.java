package ulisboa.tecnico.agents.actions.jobActions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.entityutils.entity.npc.EntityAnimation;
import ulisboa.tecnico.agents.actions.ActionStatus;
import ulisboa.tecnico.agents.actions.TemporalAction;
import ulisboa.tecnico.agents.actions.otherActions.GoTo;
import ulisboa.tecnico.agents.npc.IAgent;
import ulisboa.tecnico.agents.utils.BlockUtils;
import ulisboa.tecnico.agents.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Farming<T extends IAgent> extends TemporalAction<T> {

    // Private attributes

    private final int maxFarmingTicks;
    private final double maxFarmingRadius;
    private final Plugin plugin;
    private List<Block> farmlandBlocks;
    private GoTo<T> goingToCrop;
    private ItemStack hoe;
    private int lastActionTicks = Integer.MIN_VALUE;

    private static final Map<Material, Material> CROP_BLOCK_TO_ITEM = Map.of(
            Material.WHEAT, Material.WHEAT_SEEDS,
            Material.CARROTS, Material.CARROT,
            Material.POTATOES, Material.POTATO,
            Material.BEETROOTS, Material.BEETROOT_SEEDS
    );
    private static final Map<Material, Material> CROP_ITEM_TO_BLOCK = CROP_BLOCK_TO_ITEM.entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    private static final Random RANDOM = new Random();

    // Constructors

    public Farming(int maxFarmingTicks, double maxFarmingRadius, Plugin plugin) {
        this.maxFarmingTicks = maxFarmingTicks;
        this.maxFarmingRadius = maxFarmingRadius;
        this.plugin = plugin;
    }

    // Other methods

    @Override
    public void start(T actioner) {
        farmlandBlocks = new ArrayList<>(getNearbyFarmlandBlocks(actioner));

        hoe = new ItemStack(Material.IRON_HOE);

        actioner.getAgent().setItem(hoe, EquipmentSlot.HAND);
    }

    @Override
    public ActionStatus tick(T actioner, int elapsedTicks) {
        if (elapsedTicks >= maxFarmingTicks) {
            clear(actioner);

            return ActionStatus.SUCCESS;
        }

        if (goingToCrop == null) {
            // The agent is not moving towards a crop

            int ticksSinceLastAction = elapsedTicks - lastActionTicks;

            if (ticksSinceLastAction % 20 == 0) {
                var availableSeeds = agentSeeds(actioner);

                // Checking if there are nearby plants ready to be harvested or farmland ready to be planted
                for (Block farmland : farmlandBlocks) {
                    Block up = farmland.getRelative(BlockFace.UP);

                    if (isCrop(up)) {
                        Ageable ageable = (Ageable) up.getBlockData();

                        if (ageable.getAge() == ageable.getMaximumAge()) {
                            // Found a plant ready to be harvested
                            goingToCrop = new GoTo<>(up.getLocation().add(0.5, 0, 0.5));

                            goingToCrop.act(actioner);

                            return ActionStatus.IN_PROGRESS;
                        }
                    } else if (up.getType().isAir() && !availableSeeds.isEmpty()) {
                        // Found empty farmland that can receive some seeds
                        goingToCrop = new GoTo<>(up.getLocation().add(0.5, 0, 0.5));

                        goingToCrop.act(actioner);

                        return ActionStatus.IN_PROGRESS;
                    }
                }
            } else if (ticksSinceLastAction == 10) {
                actioner.lookForward();
            }
        } else {
            // The agent is moving towards a crop
            ActionStatus goToCropStatus = goingToCrop.act(actioner);

            if (goToCropStatus.isFinished()) {
                goingToCrop = null;
            }

            if (goToCropStatus.isSuccessful()) {
                // Arrived at the farmland
                Block crop = actioner.getLocation().getBlock();

                if (isCrop(crop)) {
                    // The agent is standing on the crop
                    Ageable ageable = (Ageable) crop.getBlockData();

                    if (ageable.getAge() == ageable.getMaximumAge()) {
                        // The crop is still harvestable
                        actioner.getAgent().lookAt(crop.getLocation().add(0.5, 0, 0.5));
                        actioner.getAgent().animate(EntityAnimation.SWING_MAIN_ARM);

                        var drops = crop.getDrops(hoe);

                        DisplayUtils.createDefaultTemporaryItemDisplayForItems(
                                drops,
                                crop.getLocation().add(0.5, 1, 0.5),
                                plugin);

                        actioner.acquiredFarmingLoot(drops);

                        lastActionTicks = elapsedTicks;

                        // Playing a crop destroy sound
                        crop.getWorld().playSound(crop.getLocation().add(0.5, 0, 0.5), crop.getBlockSoundGroup().getBreakSound(), 1, 1);

                        if (actioner.hasAndRemoveItem(CROP_BLOCK_TO_ITEM.get(crop.getType()), 1)) {
                            // The agent has the seeds to replant the crop
                            ageable.setAge(0);
                            crop.setBlockData(ageable);
                        } else {
                            crop.setType(Material.AIR);
                        }
                    }
                } else if (crop.getType().isAir()) {
                    // Found empty farmland
                    var availableSeeds = agentSeeds(actioner);

                    if (!availableSeeds.isEmpty()) {
                        // The agent has some seeds to plant. Randomly choosing one
                        Material seed = availableSeeds.get(RANDOM.nextInt(availableSeeds.size()));

                        if (actioner.hasAndRemoveItem(seed, 1)) {
                            crop.setType(CROP_ITEM_TO_BLOCK.get(seed));

                            actioner.getAgent().lookAt(crop.getLocation().add(0.5, 0, 0.5));
                            actioner.getAgent().animate(EntityAnimation.SWING_MAIN_ARM);

                            lastActionTicks = elapsedTicks;

                            // Playing a crop plant sound
                            crop.getWorld().playSound(crop.getLocation().add(0.5, 0, 0.5), crop.getBlockSoundGroup().getPlaceSound(), 1, 1);
                        }
                    }
                }
            }
        }

        return ActionStatus.IN_PROGRESS;
    }

    private List<Material> agentSeeds(T actioner) {
        List<Material> availableSeeds = new ArrayList<>(CROP_BLOCK_TO_ITEM.size());

        for (Material seed : CROP_BLOCK_TO_ITEM.values()) {
            if (actioner.hasItem(seed)) {
                availableSeeds.add(seed);
            }
        }

        return availableSeeds;
    }

    @Override
    public void cancel(T actioner) {
        clear(actioner);
    }

    public void clear(T actioner) {
        actioner.getAgent().setItem(null, EquipmentSlot.HAND);
    }

    @Override
    public boolean canBeExecuted(T actioner) {
        return !getNearbyFarmlandBlocks(actioner).isEmpty();
    }

    protected List<Block> getNearbyFarmlandBlocks(T actioner) {
        return getNearbyFarmlandBlocks(actioner.getLocation());
    }

    protected List<Block> getNearbyFarmlandBlocks(Location location) {
        Location corner1 = location.clone().add(-maxFarmingRadius, -3, -maxFarmingRadius);
        Location corner2 = location.clone().add(maxFarmingRadius, 2, maxFarmingRadius);

        var blocks = BlockUtils.getBlocksBetween(corner1, corner2);

        blocks.removeIf(block -> block.getType() != Material.FARMLAND);

        return blocks;
    }

    private boolean isCrop(Block block) {
        return CROP_BLOCK_TO_ITEM.containsKey(block.getType());
    }
}
