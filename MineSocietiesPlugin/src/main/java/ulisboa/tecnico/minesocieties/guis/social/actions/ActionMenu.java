package ulisboa.tecnico.minesocieties.guis.social.actions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.actions.jobActions.InformativeFarming;
import ulisboa.tecnico.minesocieties.agents.actions.jobActions.InformativeGoFishing;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.Idle;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.InformativeGoTo;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.GiveItemTo;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.CharacterReference;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenu;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenuOpener;
import ulisboa.tecnico.minesocieties.guis.common.GoBack;
import ulisboa.tecnico.minesocieties.guis.social.locations.AgentLocationsMenu;

public class ActionMenu extends GUIMenu {

    // Private attributes

    private final SocialAgent agent;

    // Constructors

    public ActionMenu(SocialPlayer player, SocialAgent agent) {
        super(player, "Choose an action for " + agent.getName() + " to execute", 27);

        this.agent = agent;
    }

    // Other methods

    @Override
    public void fillShopWithClickables() {
        // Go home
        addClickable(10, new ActionExecutorItem(this, Material.RED_BED, agent, new InformativeGoTo(agent.getState().getMemory().getHome().getLocation(), false)));
        addClickable(11, new KnowLocationsOpener());
        addClickable(13, new ActionExecutorItem(this, Material.FISHING_ROD, agent, new InformativeGoFishing(3, 40 * 20, 15 * 20)));
        addClickable(14, new ActionExecutorItem(this, Material.IRON_HOE, agent, new InformativeFarming(60 * 20)));
        addClickable(16, new ActionExecutorItem(this, Material.BARRIER, agent, new Idle()));

        if (MineSocieties.getPlugin().isDebugMode()) {
            // For debugging certain actions
            GiveItemTo giveItemTo = new GiveItemTo();

            giveItemTo.setItem(new ItemStack(Material.SALMON, 1));
            giveItemTo.setReceiver(new CharacterReference(getPlayer()));

            addClickable(17, new ActionExecutorItem(this, Material.SALMON, agent, giveItemTo));
        }

        addClickable(26, new GoBack(this));

        fillRestWithPanes(Material.YELLOW_STAINED_GLASS_PANE);
    }

    // Private classes

    private class KnowLocationsOpener extends GUIMenuOpener {

            // Constructors

            public KnowLocationsOpener() {
                super(ActionMenu.this, Material.RECOVERY_COMPASS, new GoToKnownLocationsMenu(getPlayer(), agent),
                        ChatColor.AQUA + "Click to see more locations:");

                AgentLocationsMenu.addLocationsToDescription(this, agent.getState().getMemory().getKnownLocations().getMemorySections(), 6);
            }
    }
}
