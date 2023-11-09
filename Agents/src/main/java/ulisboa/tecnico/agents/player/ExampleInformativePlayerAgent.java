package ulisboa.tecnico.agents.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import ulisboa.tecnico.agents.ICharacter;
import ulisboa.tecnico.agents.observation.IObservation;
import ulisboa.tecnico.agents.observation.WeatherChangeObservation;

public class ExampleInformativePlayerAgent implements IPlayerAgent {

    // Private attributes

    private Player player;

    // Constructors

    public ExampleInformativePlayerAgent(Player player) {
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

    // Other methods

    @Override
    public void observeWeatherChange(WeatherChangeObservation observation) {
        sendMessage("The weather just changed to " + observation.getWeatherType());
    }

    @Override
    public void receivedChatFrom(ICharacter from, String chat) {
        sendMessage(from.getName() + " sent you a message: " + chat);
    }

    @Override
    public void receivedAnyObservation(IObservation<?> observation) {
        // This agent only reacts to concrete observations
    }

    public void sendMessage(String message) {
        player.sendMessage(
                Component.text("[EIPA] ")
                        .color(TextColor.color(255, 149, 78))
                        .append(
                                Component.text(message)
                                        .color(TextColor.color(255, 255, 255))
                        )
        );
    }
}
