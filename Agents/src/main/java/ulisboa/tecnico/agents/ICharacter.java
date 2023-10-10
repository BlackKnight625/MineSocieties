package ulisboa.tecnico.agents;

import ulisboa.tecnico.agents.observation.WeatherChangeObservation;

import java.util.UUID;

public interface ICharacter {

    String getName();

    UUID getUUID();

    boolean isValid();

    // Observation related

    void observeWeatherChange(WeatherChangeObservation observation);

    void receivedChatFrom(ICharacter from, String chat);
}
