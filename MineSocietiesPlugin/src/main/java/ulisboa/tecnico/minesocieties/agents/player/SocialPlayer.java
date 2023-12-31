package ulisboa.tecnico.minesocieties.agents.player;

import org.bukkit.entity.Player;
import ulisboa.tecnico.agents.ICharacter;
import ulisboa.tecnico.agents.observation.IObservation;
import ulisboa.tecnico.agents.observation.IObserver;
import ulisboa.tecnico.agents.observation.ReceivedChatObservation;
import ulisboa.tecnico.agents.observation.WeatherChangeObservation;
import ulisboa.tecnico.agents.player.IPlayerAgent;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.SocialCharacter;
import ulisboa.tecnico.minesocieties.agents.observation.wrapped.SocialReceivedChatFromObservation;
import ulisboa.tecnico.minesocieties.agents.observation.wrapped.SocialWeatherChangeObservation;
import ulisboa.tecnico.minesocieties.utils.ComponentUtils;

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
    public void observeWeatherChange(SocialWeatherChangeObservation observation) {
        // Do nothing
    }

    @Override
    public void receivedChatFrom(SocialReceivedChatFromObservation observation) {
        if (!MineSocieties.getPlugin().isChatBroadcasted()) {
            // Message isn't being broadcasted, so it must be sent to this player instead
            getPlayer().sendMessage(ComponentUtils.sendMessageToPrefix(
                    observation.getObservation().getFrom().getName(),
                    getName(),
                    observation.getObservation().getChat()));
        }
    }

    // Other methods
}
