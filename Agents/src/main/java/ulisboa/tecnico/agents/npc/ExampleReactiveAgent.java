package ulisboa.tecnico.agents.npc;

import org.bukkit.inventory.ItemStack;
import org.entityutils.entity.npc.player.AnimatedPlayerNPC;
import ulisboa.tecnico.agents.ICharacter;
import ulisboa.tecnico.agents.observation.IObservation;
import ulisboa.tecnico.agents.observation.IObserver;
import ulisboa.tecnico.agents.observation.ReceivedChatObservation;
import ulisboa.tecnico.agents.observation.WeatherChangeObservation;

import java.util.Collection;

public class ExampleReactiveAgent implements IAgent {

    // Private attributes

    private final AnimatedPlayerNPC npc;

    // Constructors

    public ExampleReactiveAgent(AnimatedPlayerNPC npc) {
        this.npc = npc;
    }

    // Getters and setters

    @Override
    public AnimatedPlayerNPC getAgent() {
        return npc;
    }

    // Other methods


    @Override
    public void deploy() {
        this.npc.setAlive(true);
    }

    @Override
    public void receivedChatFrom(ReceivedChatObservation observation) {

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
    public void deleted() {
        // Do nothing
    }

    @Override
    public void acquiredFishLoot(Collection<ItemStack> fishLoot) {
        // Do nothing
    }
}
