package ulisboa.tecnico.agents.observation;

import org.bukkit.WeatherType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import ulisboa.tecnico.agents.AbstractAgentManager;

public class EventListener implements Listener {

    // Private attributes

    private final AbstractAgentManager<?, ?, ?> manager;

    // Constructors

    public EventListener(AbstractAgentManager<?, ?, ?> manager) {
        this.manager = manager;
    }

    // Getters and setters

    public AbstractAgentManager<?, ?, ?> getManager() {
        return this.manager;
    }

    // Other methods

    public void register() {
        manager.getPlugin().getServer().getPluginManager().registerEvents(this, manager.getPlugin());
    }

    // Event listeners

    @EventHandler
    public void weatherChanges(WeatherChangeEvent e) {
        WeatherType weatherType = e.toWeatherState() ? WeatherType.DOWNFALL : WeatherType.CLEAR;

        WeatherChangeObservation observation = new WeatherChangeObservation(weatherType);

        manager.forEachValidCharacter(
                observation::accept
        );
    }

    @EventHandler
    public void playerJoins(PlayerJoinEvent e) {
        manager.registerPlayer(e.getPlayer());
    }
}
