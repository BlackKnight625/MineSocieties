package ulisboa.tecnico.agents.actions.jobActions;

import jline.internal.Log;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import ulisboa.tecnico.agents.actions.ActionStatus;
import ulisboa.tecnico.agents.actions.TemporalAction;
import ulisboa.tecnico.agents.actions.jobActions.subactions.Fish;
import ulisboa.tecnico.agents.actions.otherActions.GoTo;
import ulisboa.tecnico.agents.npc.IAgent;
import ulisboa.tecnico.agents.utils.BlockUtils;

import java.util.List;
import java.util.logging.Logger;

public class GoFishing<T extends IAgent> extends TemporalAction<T> {

    // Private attributes

    private GoTo<T> goToWater;
    private Fish<T> fishAction;
    private int currentAmountFished = 0;
    private boolean reachedWater = false;
    private final int amountToFish;
    private final int maxFishingTicks;
    private final int maxTicksPerFish;
    private final Logger logger;
    private Block waterBlock;

    private static final int MAX_FISHING_DISTANCE = 5;

    // Constructors

    public GoFishing(int amountToFish, int maxFishingTicks, int maxTicksPerFish, Logger logger) {
        this.amountToFish = amountToFish;
        this.maxFishingTicks = maxFishingTicks;
        this.maxTicksPerFish = maxTicksPerFish;
        this.logger = logger;
    }

    // Other methods

    @Override
    public void start(T actioner) {
        // Finding the closest water block
        List<Block> accessibleWaterBlocks = getAccessibleWaterBlocks(actioner);

        if (accessibleWaterBlocks.isEmpty()) {
            logger.warning("Could not find any accessible water blocks near " + actioner.getName() + " so that it can fish. " +
                    "Searched for water blocks within " + MAX_FISHING_DISTANCE + " blocks of the agent's location: " + actioner.getLocation());

            return;
        }

        waterBlock = accessibleWaterBlocks.get(0);
        double closestDistance = Double.MAX_VALUE;

        for (Block block : accessibleWaterBlocks) {
            double distance = block.getLocation().distanceSquared(actioner.getLocation());

            if (distance < closestDistance) {
                waterBlock = block;
                closestDistance = distance;
            }
        }

        // Finding a solid block nearest to the water block
        Location closestWaterLocation = waterBlock.getLocation().add(0.5, 1, 0.5);
        Vector direction = actioner.getLocation().toVector().subtract(closestWaterLocation.toVector()).normalize();

        BlockIterator blockIterator = new BlockIterator(
                waterBlock.getWorld(),
                closestWaterLocation.toVector(), // Water block location
                direction,
                0,
                MAX_FISHING_DISTANCE
        );

        Block solidNearWater = null;
        boolean foundBlock = false;

        while (blockIterator.hasNext()) {
            solidNearWater = blockIterator.next();

            // Checking if the block is solid
            if (solidNearWater.isSolid()) {
                foundBlock = true;

                break;
            }
        }

        if (solidNearWater == null || !foundBlock) {
            // The NPC is inside the water or something
            logger.warning("Could not find a solid block near the water so that " + actioner.getName() + " can fish. " +
                    "Searched for a solid block within " + MAX_FISHING_DISTANCE + " blocks of the water at " + waterBlock.getLocation());

            return;
        }

        // Making sure the block is accessible
        solidNearWater = findNearestAccessibleBlock(solidNearWater, 3);

        if (solidNearWater == null) {
            // Could not find an accessible block near the water
            logger.warning("Could not find an accessible block near the water so that " + actioner.getName() + " can fish. " +
                    "Searched for an accessible block within " + MAX_FISHING_DISTANCE + " blocks of the water at " + waterBlock.getLocation());

            return;
        }

        // Sending the NPC to the block near the water
        goToWater = new GoTo<>(solidNearWater.getLocation().add(0.5, 1, 0.5));

        goToWater.start(actioner);
    }

    public @Nullable Block findNearestAccessibleBlock(Block block, int depthLeft) {
        if (depthLeft < 0) {
            return null;
        }

        if (block.isSolid()) {
            Block upOne = block.getRelative(BlockFace.UP);

            if (upOne.getType().isAir()) {
                Block upTwo = upOne.getRelative(BlockFace.UP);

                if (upTwo.getType().isAir()) {
                    // Found a block with 2 air blocks above it, making it accessible
                    return block;
                }
            }
        }

        // The block is not accessible. Returning a recursive depth-first search
        Block[] adjacentBlocks = new Block[]{
                block.getRelative(BlockFace.UP),
                block.getRelative(BlockFace.NORTH),
                block.getRelative(BlockFace.SOUTH),
                block.getRelative(BlockFace.EAST),
                block.getRelative(BlockFace.WEST),
                block.getRelative(BlockFace.DOWN)
        };

        int depthMinusOne = depthLeft - 1;

        for (Block adjacentBlock : adjacentBlocks) {
            Block foundBlock = findNearestAccessibleBlock(adjacentBlock, depthMinusOne);

            if (foundBlock != null) {
                return foundBlock;
            }
        }

        // Did not find an accessible block
        return null;
    }

    @Override
    public ActionStatus tick(T actioner, int elapsedTicks) {
        if (goToWater == null) {
            // Something went wrong at the start
            return ActionStatus.FAILURE;
        }

        if (elapsedTicks > maxFishingTicks) {
            // Agent is done fishing
            return ActionStatus.SUCCESS;
        }

        if (currentAmountFished >= amountToFish) {
            // Agent has fished enough
            return ActionStatus.SUCCESS;
        }

        if (reachedWater) {
            // Agent is near the water. Continue fishing
            if (fishAction == null) {
                fishAction = new Fish<>(maxTicksPerFish, waterBlock);

                fishAction.start(actioner);
            }

            ActionStatus fishStatus = fishAction.tick(actioner, elapsedTicks);

            if (fishStatus.isFinished()) {
                currentAmountFished++;

                if (currentAmountFished >= amountToFish) {
                    // Agent has fished enough
                    return ActionStatus.SUCCESS;
                }

                fishAction = new Fish<>(maxTicksPerFish, waterBlock);

                fishAction.start(actioner);
            }

            return ActionStatus.IN_PROGRESS;
        } else {
            ActionStatus goToStatus = goToWater.tick(actioner, elapsedTicks);

            if (goToStatus.isFinished()) {
                reachedWater = true;
            }

            return ActionStatus.IN_PROGRESS;
        }
    }

    @Override
    public boolean canBeExecuted(T actioner) {
        // This can be executed if the agent is near at least 1 accessible water block

        return !getAccessibleWaterBlocks(actioner).isEmpty();
    }

    public List<Block> getAccessibleWaterBlocks(T actioner) {
        List<Block> nearbyBlocks = BlockUtils.getNearbyBlocks(actioner.getLocation(), MAX_FISHING_DISTANCE);

        // Filter the blocks to only keep the ones that are water
        nearbyBlocks.removeIf(block -> block.getType() != Material.WATER);

        Location eyeLocation = actioner.getEyeLocation();

        // Must check what blocks are accessible
        nearbyBlocks.removeIf(block -> {
            // Water blocks without air on top are not accessible
            if (!block.getRelative(BlockFace.UP).getType().isAir()) {
                return false;
            }

            // Water blocks without a line of sight from the agent's eyes are not accessible
            Location blockCenter = block.getLocation().add(0.5, 0.5, 0.5);

            RayTraceResult result = eyeLocation.getWorld().rayTraceBlocks(eyeLocation, blockCenter.toVector().subtract(eyeLocation.toVector()), 5);

            if (result == null || result.getHitBlock() == null) {
                // Somehow, the ray did not hit anything
                return false;
            }

            // The block is accessible if the ray hit it
            return result.getHitBlock().equals(block);
        });

        return nearbyBlocks;
    }

    @Override
    public void cancel(T actioner) {
        if (goToWater != null) {
            goToWater.cancel(actioner);
        }

        if (fishAction != null) {
            fishAction.cancel(actioner);
        }
    }
}