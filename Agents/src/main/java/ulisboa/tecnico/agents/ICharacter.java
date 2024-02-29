package ulisboa.tecnico.agents;

import org.bukkit.Location;
import ulisboa.tecnico.agents.observation.IObserver;
import ulisboa.tecnico.agents.observation.WeatherChangeObservation;

import java.util.UUID;

public interface ICharacter extends IObserver {

    String getName();

    UUID getUUID();

    boolean isValid();

    Location getLocation();

    Location getEyeLocation();
}
