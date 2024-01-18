package ulisboa.tecnico.minesocieties.guis.social.information;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.guis.common.GUIDecoration;

public class UUIDItem extends GUIDecoration {

    // Constructors

    public UUIDItem(AgentInformationMenu menu, SocialAgent agent) {
        super(menu, Material.COMMAND_BLOCK, ChatColor.GRAY + "UUID: " + ChatColor.BLUE + agent.getUUID().toString());

        addDescription("", ChatColor.RED + "Cannot be edited");
    }
}
