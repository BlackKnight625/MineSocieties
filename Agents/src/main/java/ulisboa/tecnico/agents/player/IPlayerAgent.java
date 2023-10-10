package ulisboa.tecnico.agents.player;

import org.bukkit.entity.Player;
import ulisboa.tecnico.agents.ICharacter;

import java.util.UUID;

public interface IPlayerAgent extends ICharacter {

    Player getPlayer();

    void setPlayer(Player player);

    @Override
    default boolean isValid() {
        return getPlayer().isValid();
    }

    @Override
    default String getName() {
        return getPlayer().getName();
    }

    @Override
    default UUID getUUID() {
        return getPlayer().getUniqueId();
    }
}
