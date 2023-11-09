package ulisboa.tecnico.agents.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import ulisboa.tecnico.agents.ICharacter;
import ulisboa.tecnico.agents.observation.IObservation;
import ulisboa.tecnico.agents.observation.IObserver;
import ulisboa.tecnico.agents.observation.ReceivedChatObservation;
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
    public void receivedChatFrom(ReceivedChatObservation observation) {
        sendMessage(observation.getFrom().getName() + " sent you a message: " + observation.getChat());
    }

    @Override
    public void receivedAnyObservation(IObservation<IObserver> observation) {
        // This agent only reacts to concrete observations
        observation.accept(this);
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
