package ulisboa.tecnico.minesocieties.agents.player;

import org.bukkit.entity.Player;
import ulisboa.tecnico.agents.ICharacter;
import ulisboa.tecnico.agents.observation.WeatherChangeObservation;
import ulisboa.tecnico.agents.player.IPlayerAgent;
import ulisboa.tecnico.minesocieties.agents.SocialCharacter;

public class SocialPlayer extends SocialCharacter implements IPlayerAgent {

    // Private attributes

    private Player player;

    // Constructors

    public SocialPlayer(Player player) {
        this.player = player;
    }

    // Getters and setters

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    // Observation methods

    @Override
    public void observeWeatherChange(WeatherChangeObservation observation) {
        // Do nothing
    }

    @Override
    public void receivedChatFrom(ICharacter from, String chat) {

    }

    // Other methods
}
