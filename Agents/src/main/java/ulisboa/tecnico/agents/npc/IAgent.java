package ulisboa.tecnico.agents.npc;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.entityutils.entity.npc.player.AnimatedPlayerNPC;
import org.entityutils.utils.data.PlayerNPCData;
import ulisboa.tecnico.agents.ICharacter;
import ulisboa.tecnico.agents.observation.IObserver;

import java.util.Collection;
import java.util.UUID;

public interface IAgent extends ICharacter {

    /**
     *  Returns the underlying wrapper for the fake player that is interacting with the world
     * @return
     *  The fake player
     */
    AnimatedPlayerNPC getAgent();

    /**
     *  Returns data concerning the fake player being controlled by this instance
     * @return
     *  The data
     */
    default PlayerNPCData getNPCData() {
        return getAgent().getData();
    }

    @Override
    default String getName() {
        return getNPCData().getName();
    }

    @Override
    default UUID getUUID() {
         return getNPCData().getUUID();
    }

    default void deploy() {
        getAgent().setAlive(true);
    }

    void deleted();

    @Override
    default boolean isValid() {
        return getNPCData().isValid();
    }

    @Override
    default Location getLocation() {
        return getAgent().getData().getLocation().clone();
    }

    @Override
    default Location getEyeLocation() {
        Location location = getLocation();
        location.setY(location.getY() + getEyeHeight());
        return location;
    }

    default double getEyeHeight() {
        return getNPCData().getEyeHeight();
    }

    void acquiredFishLoot(Collection<ItemStack> fishLoot);

    void acquiredFarmingLoot(Collection<ItemStack> farmLoot);

    boolean hasItem(Material item);

    boolean hasAndRemoveItem(Material item, int amount);

    default void lookForward() {
        getAgent().setDirection(getAgent().getData().getYaw(), 0);
    }
}
