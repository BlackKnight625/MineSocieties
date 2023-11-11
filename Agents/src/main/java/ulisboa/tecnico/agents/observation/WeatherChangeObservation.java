package ulisboa.tecnico.agents.observation;

import org.bukkit.WeatherType;
import ulisboa.tecnico.agents.ICharacter;

import java.util.Objects;

public class WeatherChangeObservation implements IObservation<IObserver> {

    // Private attributes

    private final WeatherType weatherType;

    // Constructors

    public WeatherChangeObservation(WeatherType weatherType) {
        this.weatherType = weatherType;
    }

    // Getters and setters

    public WeatherType getWeatherType() {
        return weatherType;
    }

    // Other methods

    @Override
    public void accept(IObserver character) {
        character.observeWeatherChange(this);
    }

}
