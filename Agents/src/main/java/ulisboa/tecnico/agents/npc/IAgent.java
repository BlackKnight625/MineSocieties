package ulisboa.tecnico.agents.npc;

import org.entityutils.entity.npc.player.AnimatedPlayerNPC;
import org.entityutils.utils.data.PlayerNPCData;
import ulisboa.tecnico.agents.ICharacter;
import ulisboa.tecnico.agents.observation.IObserver;

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

    void deleted();

    @Override
    default boolean isValid() {
        return getNPCData().isValid();
    }
}
