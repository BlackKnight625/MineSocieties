package ulisboa.tecnico.minesocieties.guis.social.locations;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentKnownLocations;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentLocation;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenu;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenuOpener;
import ulisboa.tecnico.minesocieties.guis.common.GoBack;
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
        // Placing all locations
        fillPageFromList(PAGE_SIZE, locations.getMemorySections().stream().toList(),
                l -> new LocationEditItem(this, l.getLocation(), false));

        addClickable(45, new AllLocationsOpener());

        addClickable(53, new GoBack(this));

        if (locations.entrySizes() > PAGE_SIZE) {
            addBottomLayer();
        }

        fillRestWithPanes(Material.ORANGE_STAINED_GLASS_PANE);
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

    // Private classes

    private class AllLocationsOpener extends GUIMenuOpener {

        public AllLocationsOpener() {
            super(OtherLocationsMenu.this, Material.RECOVERY_COMPASS, new AllLocationsMenu(getPlayer()), ChatColor.GOLD + "See all locations");

            addDescription(ChatColor.GRAY,
                    "You may also run",
                    ChatColor.AQUA + "/agent locations" + ChatColor.GRAY + " to see",
                    "all locations without",
                    "needing to come to this GUI."
            );
        }
    }
}
