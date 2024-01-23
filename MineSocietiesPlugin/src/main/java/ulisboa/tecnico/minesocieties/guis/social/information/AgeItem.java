package ulisboa.tecnico.minesocieties.guis.social.information;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.guis.common.GUIItem;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenuOpener;
import ulisboa.tecnico.minesocieties.utils.StringUtils;

public class AgeItem extends GUIMenuOpener {

    // Constructors

    public AgeItem(AgentInformationMenu menu, SocialAgent agent) {
        super(menu, Material.CAKE, new AgeMenu(menu.getPlayer(), agent),
                ChatColor.GRAY + "Age: " + ChatColor.AQUA + agent.getState().getPersona().getAge());

        addDescription(
                ChatColor.GRAY + "Birthday: " + ChatColor.AQUA + StringUtils.toBirthdayString(agent.getState().getPersona().getBirthday()),
                "",
                ChatColor.GOLD + "Click to change the birth date"
        );
    }
}
