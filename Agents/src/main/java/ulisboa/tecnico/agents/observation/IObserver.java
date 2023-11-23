package ulisboa.tecnico.agents.observation;

public interface IObserver {

    void observeWeatherChange(WeatherChangeObservation observation);

    void receivedChatFrom(ReceivedChatObservation observation);
}
