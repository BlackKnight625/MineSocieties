package ulisboa.tecnico.minesocieties.guis.social.information.states;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.CollectionOfStates;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenuOpener;
import ulisboa.tecnico.minesocieties.guis.social.information.AgentInformationMenu;

public abstract class StateItem<T extends CollectionOfStates<?>> extends GUIMenuOpener {

    // Constructors

    public StateItem(AgentInformationMenu menu, Material material, SocialAgent agent, T states, String name) {
        super(menu, material, null, name);

        setMenuToOpen(getNewMenu(menu.getPlayer(), agent));

        int counter = 0;

        // Adding some states to the description
        for (String state : states.getStates()) {
            addDescription(ChatColor.DARK_GREEN + state);
            counter++;

            if (counter > 10) {
                // There are too many states
                addDescription(ChatColor.YELLOW + "Etc...");

                break;
            }
        }

        addDescription("");
        addDescription(ChatColor.GREEN + "Click to edit/view more details");
    }

    public abstract StateChangingMenu<T> getNewMenu(SocialPlayer player, SocialAgent agent);
}
