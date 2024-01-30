package ulisboa.tecnico.minesocieties.guis.social.information.memory;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.InstantMemory;
import ulisboa.tecnico.minesocieties.agents.npc.state.TemporaryMemory;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenuOpener;
import ulisboa.tecnico.minesocieties.guis.social.information.AgentInformationMenu;

import java.util.function.Function;

public abstract class MemoryItem<V extends InstantMemory, T extends TemporaryMemory<V>> extends GUIMenuOpener {

    // Constructors

    public MemoryItem(AgentInformationMenu menu, Material material, SocialAgent agent, T memory, Function<V, String> toString, String name) {
        super(menu, material, null, name);

        setMenuToOpen(getNewMenu(menu.getPlayer(), agent));

        int counter = 0;

        // Adding some memory sections to the description
        for (V memorySection : memory.getMemorySections()) {
            addDescription(ChatColor.BLUE + toString.apply(memorySection));
            counter++;

            if (counter > 10) {
                // The memory is too big
                addDescription(ChatColor.DARK_BLUE + "Etc...");

                break;
            }
        }

        addDescription("");
        addDescription(ChatColor.GREEN + "Click to edit/view more details");
    }

    public abstract MemoryChangingMenu<V> getNewMenu(SocialPlayer player, SocialAgent agent);
}
