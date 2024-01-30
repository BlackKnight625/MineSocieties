package ulisboa.tecnico.minesocieties.guis.social.information.states;

import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentPersonalities;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;

public class PersonalitiesChangingMenu extends StateChangingMenu<AgentPersonalities> {

    // Constructors

    public PersonalitiesChangingMenu(SocialPlayer player, SocialAgent agent) {
        super(player, agent, agent.getState().getPersonalities(), "Personalities");
    }
}
