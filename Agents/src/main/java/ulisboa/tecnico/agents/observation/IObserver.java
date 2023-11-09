package ulisboa.tecnico.agents.observation;

import ulisboa.tecnico.agents.ICharacter;

public interface IObserver {

    void observeWeatherChange(WeatherChangeObservation observation);

    void receivedChatFrom(ICharacter from, String chat);

    void receivedAnyObservation(IObservation<?> observation);
}
