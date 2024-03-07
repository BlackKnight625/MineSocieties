package ulisboa.tecnico.agents.actions.jobActions.subactions;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FishHook;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.entityutils.entity.npc.EntityAnimation;
import ulisboa.tecnico.agents.actions.ActionStatus;
import ulisboa.tecnico.agents.actions.TemporalAction;
import ulisboa.tecnico.agents.npc.IAgent;

public class Fish<T extends IAgent> extends TemporalAction<T> {

    // Private attributes

    private final int maxTicksPerFish;
    private final Block waterBlock;
    private FishHook fishHook;
    private boolean startedBobbing;
    private int ticksSinceBobbingStarted = 0;

    private static final int MAX_WAIT_FOR_START_BOBBING_TICKS = 40;

    // Constructors

    public Fish(int maxTicksPerFish, Block waterBlock) {
        this.maxTicksPerFish = maxTicksPerFish;
        this.waterBlock = waterBlock;
    }

    // Other methods

    @Override
    public void start(T actioner) {
        ItemStack fishingRod = new ItemStack(Material.FISHING_ROD);

        actioner.getAgent().setItem(fishingRod, EquipmentSlot.HAND);

        Vector castVelocity = waterBlock.getLocation().add(0.5, 1, 0.5).toVector().subtract(actioner.getEyeLocation().toVector());

        castVelocity.normalize().multiply(2);

        actioner.getAgent().lookAt(castVelocity);

        fishHook = actioner.getAgent().castFishingHook(castVelocity);

        // Temporary
        fishHook.setWaitTime(100);
    }

    @Override
    public ActionStatus tick(T actioner, int elapsedTicks) {
        if (startedBobbing) {
            // The hook has reached water
            ticksSinceBobbingStarted++;

            // Checking if too much time has passed
            if (elapsedTicks > maxTicksPerFish) {
                clear(actioner);

                return ActionStatus.FAILURE;
            }

            if (ticksSinceBobbingStarted >= fishHook.getWaitTime()) {
                // A fish is ready to be caught
                fishHook.pullHookedEntity();

                actioner.getAgent().animate(EntityAnimation.SWING_MAIN_ARM);

                clear(actioner);

                return ActionStatus.SUCCESS;
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
    }
}
