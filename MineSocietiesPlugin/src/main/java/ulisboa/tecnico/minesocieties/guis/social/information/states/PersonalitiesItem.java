package ulisboa.tecnico.minesocieties.guis.social.information.states;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.guis.common.GUIItem;
import ulisboa.tecnico.minesocieties.guis.social.information.AgentInformationMenu;

public class PersonalitiesItem extends GUIItem {

    // Constructors

    public PersonalitiesItem(AgentInformationMenu menu, SocialAgent agent) {
        super(menu, Material.HEART_POTTERY_SHERD, ChatColor.BLUE + "Personalities");

        for (String personality : agent.getState().getPersonalities().getStates()) {
            addDescription(ChatColor.YELLOW + personality);
        }
    }

    @Override
    public void clicked(ClickType click) {
        // TODO Open a menu to change the personalities
    }
}
