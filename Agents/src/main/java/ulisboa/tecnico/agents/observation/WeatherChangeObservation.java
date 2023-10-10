package ulisboa.tecnico.agents.observation;

import org.bukkit.WeatherType;
import ulisboa.tecnico.agents.ICharacter;

public record WeatherChangeObservation(WeatherType weatherType) implements IObservation<ICharacter> {
    @Override
    public void accept(ICharacter character) {
        character.observeWeatherChange(this);
    }
}
