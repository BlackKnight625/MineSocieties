package ulisboa.tecnico.minesocieties.guis.social;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentState;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenu;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenuOpener;
import ulisboa.tecnico.minesocieties.guis.social.actions.ActionExecutorItem;
import ulisboa.tecnico.minesocieties.guis.social.actions.ActionMenu;
import ulisboa.tecnico.minesocieties.guis.social.information.AgentInformationMenu;
import ulisboa.tecnico.minesocieties.utils.StringUtils;

/**
 * This class represents the main menu for editing a Social Agent.
 */
public class SocialAgentMainMenu extends GUIMenu {

    // Protected attributes

    protected final SocialAgent agent;

    // Constructors

    public SocialAgentMainMenu(SocialPlayer player, SocialAgent agent) {
        super(player, agent.getName() + "'s Editor", 27);

        this.agent = agent;
    }

    // Other methods

    @Override
    public void fillShopWithClickables() {
        addClickable(10, new AgentInformationMenuOpener());
        addClickable(16, new ActionMenuOpener());

        fillRestWithPanes(Material.GREEN_STAINED_GLASS_PANE);
    }

    // Private classes

    private class AgentInformationMenuOpener extends GUIMenuOpener {

        // Constructors

        public AgentInformationMenuOpener() {
            super(SocialAgentMainMenu.this, Material.PAPER, new AgentInformationMenu(getPlayer(), agent),
                    ChatColor.GOLD + agent.getName() + ChatColor.GRAY + "'s information");

            addDescription("");

            AgentState state = agent.getState();

            addDescription(ChatColor.GRAY,
                    "Name: " + ChatColor.GREEN + agent.getName(),
                    "Age: " + ChatColor.AQUA + state.getPersona().getAge(),
                    "Birthday: " + ChatColor.AQUA + StringUtils.toBirthdayString(state.getPersona().getBirthday())
            );

            addDescription("");
            addDescription(ChatColor.AQUA + "Click for more details");
        }
    }

    private class ActionMenuOpener extends GUIMenuOpener {

        // Constructors

        public ActionMenuOpener() {
            super(SocialAgentMainMenu.this, Material.GOLDEN_SWORD, new ActionMenu(getPlayer(), agent),
                    ChatColor.YELLOW + "Execute actions");

            addDescription("");
            addDescription(ChatColor.GRAY,
                    "Click to see the actions",
                    "that you can force " + agent.getName(),
                    "to excecute.");
        }
    }
}
