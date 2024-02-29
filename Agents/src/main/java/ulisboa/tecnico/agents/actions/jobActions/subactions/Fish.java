package ulisboa.tecnico.agents.actions.jobActions.subactions;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import ulisboa.tecnico.agents.actions.ActionStatus;
import ulisboa.tecnico.agents.actions.TemporalAction;
import ulisboa.tecnico.agents.npc.IAgent;

public class Fish<T extends IAgent> extends TemporalAction<T> {

    // Private attributes

    private final int maxTicksPerFish;

    // Constructors

    public Fish(int maxTicksPerFish) {
        this.maxTicksPerFish = maxTicksPerFish;
    }

    // Other methods

    @Override
    public void start(T actioner) {
        ItemStack fishingRod = new ItemStack(Material.FISHING_ROD);

        actioner.getAgent().setItem();
    }

    @Override
    public ActionStatus tick(T actioner, int elapsedTicks) {
        return null;
    }
}
