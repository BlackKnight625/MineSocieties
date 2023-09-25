package ulisboa.tecnico.agents.player;

import org.bukkit.entity.Player;
import ulisboa.tecnico.agents.ICharacter;

public interface IPlayerCharacter extends ICharacter {

    Player getPlayer();

    void setPlayer(Player player);
}
