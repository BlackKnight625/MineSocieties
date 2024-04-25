package ulisboa.tecnico.agents.actions.jobActions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.entityutils.entity.npc.EntityAnimation;
import ulisboa.tecnico.agents.actions.ActionStatus;
import ulisboa.tecnico.agents.actions.TemporalAction;
import ulisboa.tecnico.agents.actions.otherActions.GoTo;
import ulisboa.tecnico.agents.npc.IAgent;
import ulisboa.tecnico.agents.utils.BlockUtils;

import java.util.List;

public class Farming<T extends IAgent> extends TemporalAction<T> {

    // Private attributes

    private final int maxFarmingTicks;
    private final double maxFarmingRadius;
    private List<Block> farmlandBlocks;
    private GoTo<T> goingToCrop;
    private ItemStack hoe;

    // Constructors

    public Farming(int maxFarmingTicks, int maxFarmingRadius) {
        this.maxFarmingTicks = maxFarmingTicks;
        this.maxFarmingRadius = maxFarmingRadius;
    }

    // Other methods

    @Override
    public void start(T actioner) {
        farmlandBlocks = getNearbyFarmlandBlocks(actioner);

        hoe = new ItemStack(Material.IRON_HOE);

        actioner.getAgent().setItem(hoe, EquipmentSlot.HAND);
    }

    @Override
    public ActionStatus tick(T actioner, int elapsedTicks) {
        if (elapsedTicks >= maxFarmingTicks) {
            return ActionStatus.SUCCESS;
        }

        if (goingToCrop == null) {
            // The agent is not moving towards a crop

            if (elapsedTicks % 20 == 0) {
                // Checking if there are nearby plants ready to be harvested
                for (Block farmland : farmlandBlocks) {
                    Block up = farmland.getRelative(BlockFace.UP);

                    if (up.getBlockData() instanceof Ageable ageable) {
                        if (ageable.getAge() == ageable.getMaximumAge()) {
                            // Found a plant ready to be harvested
                            goingToCrop = new GoTo<>(up.getLocation().add(0.5, 0, 0.5));

                            goingToCrop.act(actioner);

                            return ActionStatus.IN_PROGRESS;
                        }
                    }
                }
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

                if (crop.getBlockData() instanceof Ageable ageable) {
                    // The agent is standing on the crop

                    if (ageable.getAge() == ageable.getMaximumAge()) {
                        // The crop is still harvestable
                        actioner.getAgent().lookAt(crop.getLocation().add(0.5, 0, 0.5));
                        actioner.getAgent().animate(EntityAnimation.SWING_MAIN_ARM);

                        // crop.getDrops(hoe);
                    }
                }
            }
        }

        return ActionStatus.IN_PROGRESS;
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
        Location corner1 = actioner.getLocation().add(-maxFarmingRadius, -3, -maxFarmingRadius);
        Location corner2 = actioner.getLocation().add(maxFarmingRadius, 2, maxFarmingRadius);

        var blocks = BlockUtils.getBlocksBetween(corner1, corner2);

        blocks.removeIf(block -> block.getType() != Material.FARMLAND);

        return blocks;
    }
}
