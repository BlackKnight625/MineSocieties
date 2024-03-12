package ulisboa.tecnico.agents.observation;

import org.bukkit.WeatherType;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import ulisboa.tecnico.agents.AbstractAgentManager;
import ulisboa.tecnico.agents.actions.jobActions.subactions.Fish;
import ulisboa.tecnico.agents.utils.DisplayUtils;

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

    @EventHandler
    public void playerFishes(PlayerFishEvent e) {
        Fish.notifyPlayerFished(e.getPlayer().getUniqueId(), e);
    }

    @EventHandler
    public void entitiesLoad(EntitiesLoadEvent e) {
        for (Entity entity : e.getEntities()) {
            // Checking if this entity is a leftover Display Entity that should have been destroyed
            if (entity instanceof Display display && DisplayUtils.isTemporaryDisplay(display)) {
                display.remove();
            }
        }
    }
}
