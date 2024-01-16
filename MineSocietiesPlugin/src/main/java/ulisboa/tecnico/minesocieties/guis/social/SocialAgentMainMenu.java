package ulisboa.tecnico.minesocieties.guis.social;

import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenu;

/**
 * This class represents the main menu for editing a Social Agent.
 */
public class SocialAgentMainMenu extends GUIMenu {

    // Protected attributes

    protected final SocialAgent agent;

    // Constructors

    public SocialAgentMainMenu(SocialPlayer player, SocialAgent agent) {
        super(player, agent.getName() + "'s Editor", 54);

        this.agent = agent;
    }

    // Other methods

    @Override
    public void fillShopWithClickables() {

    }
}
