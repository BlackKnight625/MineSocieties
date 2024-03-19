package ulisboa.tecnico.minesocieties.guis.social.actions;

import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.InformativeGoTo;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentKnownLocations;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.GoBack;
import ulisboa.tecnico.minesocieties.guis.common.PageableMenu;

public class GoToKnownLocationsMenu extends PageableMenu {

    // Private attributes

    private final SocialAgent agent;
    private final AgentKnownLocations knownLocations;

    private static final int ITEMS_PER_PAGE = 27;

    // Constructors

    public GoToKnownLocationsMenu(SocialPlayer player, SocialAgent agent) {
        super(player, "Pick a location to make this agent go to it", 36);

        this.agent = agent;
        this.knownLocations = agent.getState().getMemory().getKnownLocations();
    }

    // Other methods

    public void refreshItemsInPage() {
        // Adding an item to make agents go to every location they know
        fillPageFromList(ITEMS_PER_PAGE, knownLocations.getMemorySections().stream().toList(),
                loc -> new ActionExecutorItem(this, Material.COMPASS, agent, new InformativeGoTo(loc.getLocation())));

        addClickable(35, new GoBack(this));

        if (getMaxPages() > 1) {
            addBottomLayer();
        }

        fillRestWithPanes(Material.YELLOW_STAINED_GLASS_PANE);
    }

    @Override
    public void fillShopWithClickables() {
        refreshItemsInPage();
    }

    @Override
    public void moveToNextPage() {
        refreshItemsInPage();
    }

    @Override
    public void moveToPreviousPage() {
        refreshItemsInPage();
    }

    @Override
    public int getMaxPages() {
        return (knownLocations.entrySizes() / ITEMS_PER_PAGE) + 1;
    }
}
