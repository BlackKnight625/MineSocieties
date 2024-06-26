package ulisboa.tecnico.minesocieties.guis.social.information.states;

import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentMoods;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;

public class EmotionsMenu extends StateChangingMenu<AgentMoods> {

    // Constructors

    public EmotionsMenu(SocialPlayer player, SocialAgent agent) {
        super(player, agent, agent.getState().getMoods(), "Emotions/moods");
    }
}
