package ulisboa.tecnico.minesocieties.guis.social.information;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.LongTermMemorySection;
import ulisboa.tecnico.minesocieties.agents.npc.state.ShortTermMemorySection;
import ulisboa.tecnico.minesocieties.guis.common.GUIDecoration;

public class ShortTermMemoryItem extends GUIDecoration {

    // Constructors

    public ShortTermMemoryItem(AgentInformationMenu menu, SocialAgent agent) {
        super(menu, Material.BOOK, ChatColor.LIGHT_PURPLE + "Short term memory");

        for (ShortTermMemorySection memorySection : agent.getState().getMemory().getShortTermMemory().getMemorySections()) {
            addDescription(ChatColor.BLUE + memorySection.getMemorySection());
        }
    }
}
