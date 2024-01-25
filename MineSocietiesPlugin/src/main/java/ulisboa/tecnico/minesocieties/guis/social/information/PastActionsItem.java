package ulisboa.tecnico.minesocieties.guis.social.information;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentLongTermMemory;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentPastActions;
import ulisboa.tecnico.minesocieties.agents.npc.state.LongTermMemorySection;
import ulisboa.tecnico.minesocieties.agents.npc.state.PastAction;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.GUIDecoration;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenuOpener;

public class PastActionsItem extends MemoryItem<PastAction, AgentPastActions> {

    // Constructors

    public PastActionsItem(AgentInformationMenu menu, SocialAgent agent) {
        super(menu, Material.DIAMOND_PICKAXE, agent, agent.getState().getMemory().getPastActions(), PastAction::getPastAction, ChatColor.YELLOW + "Past actions");
    }

    // Other methods

    @Override
    public MemoryChangingMenu<PastAction> getNewMenu(SocialPlayer player, SocialAgent agent) {
        return new PastActionsChangingMenu(player, agent);
    }
}
