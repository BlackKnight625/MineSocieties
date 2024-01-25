package ulisboa.tecnico.minesocieties.guis.social.information;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentShortTermMemory;
import ulisboa.tecnico.minesocieties.agents.npc.state.LongTermMemorySection;
import ulisboa.tecnico.minesocieties.agents.npc.state.ShortTermMemorySection;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.GUIDecoration;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenuOpener;

import java.util.function.Function;

public class ShortTermMemoryItem extends MemoryItem<ShortTermMemorySection, AgentShortTermMemory> {

    // Constructors

    public ShortTermMemoryItem(AgentInformationMenu menu, SocialAgent agent) {
        super(menu, Material.BOOK, agent, agent.getState().getMemory().getShortTermMemory(), ShortTermMemorySection::getMemorySection, ChatColor.LIGHT_PURPLE + "Short term memory");
    }

    // Other methods

    @Override
    public MemoryChangingMenu<ShortTermMemorySection> getNewMenu(SocialPlayer player, SocialAgent agent) {
        return new ShortTermMemoryChangingMenu(player, agent);
    }
}
