package ulisboa.tecnico.minesocieties.guis.social.information.states;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentPersonalities;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.social.information.AgentInformationMenu;

public class PersonalitiesItem extends StateItem<AgentPersonalities> {

    // Constructors

    public PersonalitiesItem(AgentInformationMenu menu, SocialAgent agent) {
        super(menu, Material.ARMS_UP_POTTERY_SHERD, agent, agent.getState().getPersonalities(), ChatColor.BLUE + "Personalities");
    }

    // Other methods

    @Override
    public StateChangingMenu<AgentPersonalities> getNewMenu(SocialPlayer player, SocialAgent agent) {
        return new PersonalitiesMenu(player, agent);
    }
}
