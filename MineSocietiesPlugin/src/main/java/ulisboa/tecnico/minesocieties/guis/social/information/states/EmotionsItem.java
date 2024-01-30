package ulisboa.tecnico.minesocieties.guis.social.information.states;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.DecoratedPot;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.BlockStateMeta;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.Mood;
import ulisboa.tecnico.minesocieties.guis.common.GUIItem;
import ulisboa.tecnico.minesocieties.guis.social.information.AgentInformationMenu;

public class EmotionsItem extends GUIItem {

    // Constructors

    public EmotionsItem(AgentInformationMenu menu, SocialAgent agent) {
        super(menu, Material.ARMS_UP_POTTERY_SHERD, ChatColor.RED + "Emotions");

        for (String emotion : agent.getState().getMoods().getStates()) {
            addDescription(ChatColor.BLUE + emotion);
        }
    }

    @Override
    public void clicked(ClickType click) {
        // TODO Open a menu to change the emotions
    }
}
