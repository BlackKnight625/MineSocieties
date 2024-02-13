package ulisboa.tecnico.minesocieties.guis.social.information.locations;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentKnownLocations;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentLocation;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
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
                l -> new LocationEditItem(this, Material.COMPASS, agent, l, false));

        addClickable(45, new NewLocationAdder());

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

    private class NewLocationAdder extends GUIMenuOpener {

        // Constructors

        public NewLocationAdder() {
            super(OtherLocationsMenu.this, Material.RECOVERY_COMPASS,
                    null, ChatColor.GREEN + "Add new location");
        }

        // Other methods


        @Override
        public void clicked(ClickType click) {
            // Creating a new location
            AgentLocation newLocation = new AgentLocation(getPlayer().getLocation().toVector(), getPlayer().getLocation().getWorld().getName(),
                    "The description for the location. Ex: " + agent.getName() + "'s job.");

            // Saving the new unedited location
            agent.getState().getMemory().getKnownLocations().addMemorySection(newLocation);
            agent.getState().saveAsync();

            // Opening the menu to edit the new location
            setMenuToOpen(new LocationEditorMenu(getPlayer(), agent, newLocation, false));

            super.clicked(click);
        }
    }
}
