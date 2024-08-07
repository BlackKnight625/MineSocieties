package ulisboa.tecnico.minesocieties.guis.social.information.states;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentMoods;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.social.information.AgentInformationMenu;

public class EmotionsItem extends StateItem<AgentMoods> {

    // Constructors

    public EmotionsItem(AgentInformationMenu menu, SocialAgent agent) {
        super(menu, Material.HEART_POTTERY_SHERD, agent, agent.getState().getMoods(), ChatColor.RED + "Emotions");
    }

    // Other methods

    @Override
    public StateChangingMenu<AgentMoods> getNewMenu(SocialPlayer player, SocialAgent agent) {
        return new EmotionsMenu(player, agent);
    }
}
