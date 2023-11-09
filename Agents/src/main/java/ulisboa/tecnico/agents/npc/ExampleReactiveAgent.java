package ulisboa.tecnico.agents.npc;

import org.entityutils.entity.npc.player.AnimatedPlayerNPC;
import ulisboa.tecnico.agents.ICharacter;
import ulisboa.tecnico.agents.observation.IObservation;
import ulisboa.tecnico.agents.observation.WeatherChangeObservation;

public class ExampleReactiveAgent implements IAgent {

    // Private attributes

    private final AnimatedPlayerNPC npc;

    // Constructors

    public ExampleReactiveAgent(AnimatedPlayerNPC npc) {
        this.npc = npc;

        this.npc.setAlive(true);
    }

    // Getters and setters

    @Override
    public AnimatedPlayerNPC getAgent() {
        return npc;
    }

    // Other methods

    @Override
    public void receivedChatFrom(ICharacter from, String chat) {

    }

    @Override
    public void observeWeatherChange(WeatherChangeObservation observation) {
        switch (observation.getWeatherType()) {
            case DOWNFALL -> {
                // Agent will look up to watch the rain
                npc.setDirection(npc.getData().getYaw(), -45f);
            }
            case CLEAR -> {
                // Agent will look forward
                npc.setDirection(npc.getData().getYaw(), 0);
            }
        }
    }

    @Override
    public void receivedAnyObservation(IObservation<?> observation) {
        // This agent only reacts to concrete observations
    }

    @Override
    public void deleted() {
        // Do nothing
    }
}
