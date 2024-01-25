package ulisboa.tecnico.minesocieties.guis.social.information;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.*;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.GUIDecoration;

public class LongTermMemoryItem extends MemoryItem<LongTermMemorySection, AgentLongTermMemory> {

    // Constructors

    public LongTermMemoryItem(AgentInformationMenu menu, SocialAgent agent) {
        super(menu, Material.BOOKSHELF, agent, agent.getState().getMemory().getLongTermMemory(), LongTermMemorySection::getMemorySection, ChatColor.DARK_PURPLE + "Long term memory");
    }

    // Other methods

    @Override
    public MemoryChangingMenu<LongTermMemorySection> getNewMenu(SocialPlayer player, SocialAgent agent) {
        return new LongTermMemoryChangingMenu(player, agent);
    }
}
