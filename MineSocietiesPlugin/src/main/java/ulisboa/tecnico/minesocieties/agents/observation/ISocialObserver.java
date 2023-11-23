package ulisboa.tecnico.minesocieties.agents.observation;

import ulisboa.tecnico.agents.observation.IObserver;
import ulisboa.tecnico.agents.observation.ReceivedChatObservation;
import ulisboa.tecnico.agents.observation.WeatherChangeObservation;
import ulisboa.tecnico.minesocieties.agents.observation.wrapped.SocialReceivedChatFromObservation;
import ulisboa.tecnico.minesocieties.agents.observation.wrapped.SocialWeatherChangeObservation;

public interface ISocialObserver extends IObserver {

    void observeWeatherChange(SocialWeatherChangeObservation observation);

    @Override
    default void observeWeatherChange(WeatherChangeObservation observation) {
        observeWeatherChange(new SocialWeatherChangeObservation(observation));
    }

    void receivedChatFrom(SocialReceivedChatFromObservation observation);

    @Override
    default void receivedChatFrom(ReceivedChatObservation observation) {
        receivedChatFrom(new SocialReceivedChatFromObservation(observation));
    }
}
