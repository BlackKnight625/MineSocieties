package ulisboa.tecnico.minesocieties.guis.social.actions;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.agents.actions.jobActions.InformativeGoFishing;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.Idle;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.InformativeGoTo;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenu;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenuOpener;
import ulisboa.tecnico.minesocieties.guis.common.GoBack;
import ulisboa.tecnico.minesocieties.guis.social.information.locations.LocationsMenu;

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
        addClickable(10, new ActionExecutorItem(this, Material.RED_BED, agent, new InformativeGoTo(agent.getState().getMemory().getHome())));
        addClickable(11, new KnowLocationsOpener());
        addClickable(13, new ActionExecutorItem(this, Material.FISHING_ROD, agent, new InformativeGoFishing(1, 200, 200)));
        addClickable(16, new ActionExecutorItem(this, Material.BARRIER, agent, new Idle()));

        addClickable(26, new GoBack(this));

        fillRestWithPanes(Material.YELLOW_STAINED_GLASS_PANE);
    }

    // Private classes

    private class KnowLocationsOpener extends GUIMenuOpener {

            // Constructors

            public KnowLocationsOpener() {
                super(ActionMenu.this, Material.RECOVERY_COMPASS, new GoToKnownLocationsMenu(getPlayer(), agent),
                        ChatColor.AQUA + "Click to see more locations:");

                LocationsMenu.addLocationsToDescription(this, agent.getState().getMemory().getKnownLocations().getMemorySections(), 6);
            }
    }
}
