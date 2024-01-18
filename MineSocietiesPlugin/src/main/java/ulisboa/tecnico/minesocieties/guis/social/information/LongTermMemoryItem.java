package ulisboa.tecnico.minesocieties.guis.social.information;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.LongTermMemorySection;
import ulisboa.tecnico.minesocieties.agents.npc.state.PastAction;
import ulisboa.tecnico.minesocieties.guis.common.GUIDecoration;

public class LongTermMemoryItem extends GUIDecoration {

    // Constructors

    public LongTermMemoryItem(AgentInformationMenu menu, SocialAgent agent) {
        super(menu, Material.BOOKSHELF, ChatColor.DARK_PURPLE + "Long term memory");

        for (LongTermMemorySection memorySection : agent.getState().getMemory().getLongTermMemory().getMemorySections()) {
            addDescription(ChatColor.BLUE + memorySection.getMemorySection());
        }
    }
}
