package ulisboa.tecnico.minesocieties.guis.social.information;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.PastAction;
import ulisboa.tecnico.minesocieties.guis.common.GUIDecoration;

public class PastActionsItem extends GUIDecoration {

    // Constructors

    public PastActionsItem(AgentInformationMenu menu, SocialAgent agent) {
        super(menu, Material.DIAMOND_PICKAXE, ChatColor.YELLOW + "Past actions");

        for (PastAction action : agent.getState().getMemory().getPastActions().getMemorySections()) {
            addDescription(ChatColor.BLUE + action.getPastAction() + " " + ChatColor.GRAY + action.getExactHowLongAgo());
        }
    }
}
