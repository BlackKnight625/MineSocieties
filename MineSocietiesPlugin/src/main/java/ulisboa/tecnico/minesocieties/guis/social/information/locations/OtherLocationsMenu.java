package ulisboa.tecnico.minesocieties.guis.social.information.locations;

import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentKnownLocations;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.PageableMenu;

public class OtherLocationsMenu extends PageableMenu {

    // Private attributes

    private final SocialAgent agent;
    private final AgentKnownLocations locations;

    private static final int PAGE_SIZE = 45;

    // Constructors

    public OtherLocationsMenu(SocialPlayer player, SocialAgent agent) {
        super(player, "Locations", 54);

        this.agent = agent;
        this.locations = agent.getState().getMemory().getKnownLocations();
    }

    // Other methods

    @Override
    public void fillShopWithClickables() {
        reloadItemsInPage();
    }

    private void reloadItemsInPage() {

    }

    @Override
    public void moveToNextPage() {
        reloadItemsInPage();
    }

    @Override
    public void moveToPreviousPage() {
        reloadItemsInPage();
    }

    @Override
    public int getMaxPages() {
        return (locations.entrySizes() / PAGE_SIZE) + 1;
    }
}
