package ulisboa.tecnico.minesocieties.agents.npc;

import org.entityutils.entity.npc.player.AnimatedPlayerNPC;
import ulisboa.tecnico.agents.ICharacter;
import ulisboa.tecnico.agents.npc.IAgent;
import ulisboa.tecnico.agents.observation.IObservation;
import ulisboa.tecnico.agents.observation.WeatherChangeObservation;
import ulisboa.tecnico.minesocieties.agents.SocialCharacter;

public class SocialAgent extends SocialCharacter implements IAgent {

    // Private attributes

    private final AnimatedPlayerNPC npc;

    // Constructors

    public SocialAgent(AnimatedPlayerNPC npc) {
        this.npc = npc;

        this.npc.setAlive(true);
    }

    // Getters and setters

    @Override
    public AnimatedPlayerNPC getAgent() {
        return npc;
    }

    // Observation methods

    @Override
    public void observeWeatherChange(WeatherChangeObservation observation) {

    }

    @Override
    public void receivedChatFrom(ICharacter from, String chat) {

    }

    @Override
    public void receivedAnyObservation(IObservation<?> observation) {
        // This agent will prompt the LLM to know whether they should react to this observation
    }

    public void chooseNewAction() {

    }

    // Other methods

    @Override
    public void deleted() {

    }
}
