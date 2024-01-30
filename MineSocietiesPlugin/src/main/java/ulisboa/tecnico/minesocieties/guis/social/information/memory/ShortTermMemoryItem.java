package ulisboa.tecnico.minesocieties.guis.social.information.memory;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentShortTermMemory;
import ulisboa.tecnico.minesocieties.agents.npc.state.ShortTermMemorySection;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.social.information.AgentInformationMenu;

public class ShortTermMemoryItem extends MemoryItem<ShortTermMemorySection, AgentShortTermMemory> {

    // Constructors

    public ShortTermMemoryItem(AgentInformationMenu menu, SocialAgent agent) {
        super(menu, Material.BOOK, agent, agent.getState().getMemory().getShortTermMemory(), ShortTermMemorySection::getMemorySection, ChatColor.LIGHT_PURPLE + "Short term memory");
    }

    // Other methods

    @Override
    public MemoryChangingMenu<ShortTermMemorySection> getNewMenu(SocialPlayer player, SocialAgent agent) {
        return new ShortTermMemoryMenu(player, agent);
    }
}
