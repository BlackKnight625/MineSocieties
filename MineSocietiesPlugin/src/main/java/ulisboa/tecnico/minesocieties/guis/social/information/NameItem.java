package ulisboa.tecnico.minesocieties.guis.social.information;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.guis.common.GUIDecoration;

public class NameItem extends GUIDecoration {

    // Constructors

    public NameItem(AgentInformationMenu menu, SocialAgent agent) {
        super(menu, Material.OAK_SIGN, ChatColor.GRAY + "Name: " + ChatColor.GREEN + agent.getName());

        addDescription("", ChatColor.RED + "Cannot be edited");
    }
}
