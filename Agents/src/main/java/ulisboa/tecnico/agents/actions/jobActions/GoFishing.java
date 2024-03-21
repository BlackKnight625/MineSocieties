package ulisboa.tecnico.agents.actions.jobActions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
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
    private final Plugin plugin;

    private static final int MAX_FISHING_DISTANCE = 5;

    // Constructors

    public GoFishing(int amountToFish, int maxFishingTicks, int maxTicksPerFish, Logger logger, Plugin plugin) {
        this.amountToFish = amountToFish;
        this.maxFishingTicks = maxFishingTicks;
        this.maxTicksPerFish = maxTicksPerFish;
        this.logger = logger;
        this.plugin = plugin;
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

        // Sorting the accessible blocks by distance to the player
        accessibleWaterBlocks.sort((block1, block2) -> {
            double distance1 = block1.getLocation().add(0.5, 1, 0.5).distanceSquared(actioner.getLocation());
            double distance2 = block2.getLocation().add(0.5, 1, 0.5).distanceSquared(actioner.getLocation());

            return Double.compare(distance1, distance2);
        });

        Block bestBlockNearWater = null;

        for (Block candidateWaterBlock : accessibleWaterBlocks) {
            // Finding a solid block nearest to the water block
            Location waterBlockLocation = candidateWaterBlock.getLocation().add(0.5, 1, 0.5);
            Vector halfWay = actioner.getLocation().toVector().add(waterBlockLocation.toVector()).multiply(0.5);

            Location middleLocation = new Location(candidateWaterBlock.getWorld(), halfWay.getX(), halfWay.getY(), halfWay.getZ());
            int searchRadius = (int) Math.ceil(halfWay.distance(middleLocation.toVector()) + 1); // Searching a bit further than the middle

            bestBlockNearWater = null;
            double bestDistanceToWater = Double.MAX_VALUE;

            // Looking for the best block that the NPC can stand on to fish
            for (Block candidate : BlockUtils.getNearbyBlocks(middleLocation, searchRadius)) {
                if (!candidate.isSolid()) {
                    continue;
                }

                Block upOne = candidate.getRelative(BlockFace.UP);

                if (!upOne.getType().isAir()) {
                    continue;
                }

                Block upTwo = upOne.getRelative(BlockFace.UP);

                if (!upTwo.getType().isAir()) {
                    continue;
                }

                Location candidateLocation = candidate.getLocation().add(0.5, 1, 0.5);

                // Found a block that the NPC can go to. Comparing it with the current best one
                if (bestBlockNearWater == null && hasLineOfSight(actioner, candidateLocation, waterBlockLocation)) {
                    bestBlockNearWater = candidate;
                    bestDistanceToWater = candidateLocation.distanceSquared(waterBlockLocation);
                } else {
                    double distanceToWater = candidateLocation.distanceSquared(waterBlockLocation);

                    if (distanceToWater < bestDistanceToWater && hasLineOfSight(actioner, candidateLocation, waterBlockLocation)) {
                        bestBlockNearWater = candidate;
                        bestDistanceToWater = distanceToWater;
                    }
                }
            }

            if (bestBlockNearWater != null) {
                // Found a water block that is accessible
                waterBlock = candidateWaterBlock;
                break;
            }
        }

        if (waterBlock == null) {
            logger.warning("Could not find any accessible water blocks near " + actioner.getName() + " so that it can fish. " +
                    "Searched for water blocks within " + MAX_FISHING_DISTANCE + " blocks of the agent's location: " + actioner.getLocation());

            return;
        }

        // Sending the NPC to the block near the water
        goToWater = new GoTo<>(bestBlockNearWater.getLocation().add(0.5, 1, 0.5));

        goToWater.act(actioner);
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
                fishAction = new Fish<>(maxTicksPerFish, waterBlock, plugin);
            } else {
                ActionStatus fishStatus = fishAction.act(actioner);

                if (fishStatus.isSuccessful()) {
                    currentAmountFished++;

                    if (currentAmountFished >= amountToFish) {
                        // Agent has fished enough
                        return ActionStatus.SUCCESS;
                    }
                    fishAction = new Fish<>(maxTicksPerFish, waterBlock, plugin);
                } else if (fishStatus.isFinished() && elapsedTicks % 20 == 0) {
                    // Trying again
                    fishAction = new Fish<>(maxTicksPerFish, waterBlock, plugin);
                }
            }

            return ActionStatus.IN_PROGRESS;
        } else {
            // Agent is still on their way to the water
            ActionStatus goToStatus = goToWater.act(actioner);

            if (goToStatus.isFinished()) {
                reachedWater = true;
            }

            return ActionStatus.IN_PROGRESS;
        }
    }

    /**
     *  Checks if the NPC standing on the candidateBlock has a line of sight to the waterBlock
     * @param actioner
     *  The NPC
     * @param candidateBlock
     *  The block where the NPC would be standing
     * @param waterLocation
     *  The location of the middle upper water block
     * @return
     *  True if the NPC would have a line of sight to the water block, false otherwise
     */
    public boolean hasLineOfSight(T actioner, Location candidateBlock, Location waterLocation) {
        Location eyeLocation = candidateBlock.clone().add(0, actioner.getEyeHeight(), 0);
        double maxDistance = eyeLocation.distance(waterLocation);

        RayTraceResult result = eyeLocation.getWorld().rayTraceBlocks(
                eyeLocation,
                waterLocation.toVector().subtract(eyeLocation.toVector()),
                maxDistance
        );

        return result == null; // If the ray did not hit anything, the NPC has line of sight
    }

    @Override
    public boolean canBeExecuted(T actioner) {
        // This can be executed if the agent is near at least 1 accessible water block

        return !getAccessibleWaterBlocks(actioner).isEmpty();
    }

    public List<Block> getAccessibleWaterBlocks(T actioner) {
        return getAccessibleWaterBlocks(actioner.getLocation());
    }

    public List<Block> getAccessibleWaterBlocks(Location location) {
        List<Block> nearbyBlocks = BlockUtils.getNearbyBlocks(location, MAX_FISHING_DISTANCE);

        // Filter the blocks to only keep the ones that are water
        nearbyBlocks.removeIf(block -> block.getType() != Material.WATER);

        // Must check what blocks are accessible
        nearbyBlocks.removeIf(block -> {
            // Water blocks without air on top are not accessible
            return block.getRelative(BlockFace.UP).getType().isAir();
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
