package ulisboa.tecnico.minesocieties.guis.social.information.locations;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.location.PublicAccess;
import ulisboa.tecnico.minesocieties.agents.location.SocialLocation;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenuOpener;
import ulisboa.tecnico.minesocieties.guis.common.GoBack;
import ulisboa.tecnico.minesocieties.guis.common.PageableMenu;

import java.util.Comparator;
import java.util.List;

public class AllLocationsMenu extends PageableMenu {

    // Private attributes

    private List<SocialLocation> locations;

    private static final int MAX_LOCATIONS_PER_PAGE = 45;

    // Constructors

    public AllLocationsMenu(SocialPlayer player, String name, int size) {
        super(player, name, size);

        locations = MineSocieties.getPlugin().getLocationsManager().getAllLocations();

        sortLocations();
    }

    private void sortLocations() {
        // Sorting based on names
        locations.sort(Comparator.comparing(SocialLocation::getName));
    }

    public void reloadItemsInPage() {
        // Adding items to edit the locations
        fillPageFromList(
                MAX_LOCATIONS_PER_PAGE,
                locations,
                location -> new LocationEditItem(
                        this,
                        location,
                        location.getStronglyConnectedAgentsCopy().stream().anyMatch( // Editting should be limited if it's an agent's home
                                agent -> ((SocialAgent)agent.getReferencedCharacter()).getState().getMemory().getHome().equals(location.toReference())
                        )
                )
        );

        addClickable(45, new NewLocationAdder());
        addClickable(53, new GoBack(this));

        if (locations.size() > MAX_LOCATIONS_PER_PAGE) {
            addBottomLayer();
        }
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
        return (locations.size() / MAX_LOCATIONS_PER_PAGE) + 1;
    }

    @Override
    public void fillShopWithClickables() {
        reloadItemsInPage();
    }

    // Private classes

    private class NewLocationAdder extends GUIMenuOpener {

        // Constructors

        public NewLocationAdder() {
            super(AllLocationsMenu.this, Material.RECOVERY_COMPASS,
                    null, ChatColor.GREEN + "Add new location");
        }

        // Other methods


        @Override
        public void clicked(ClickType click) {
            // Creating a new location
            SocialLocation newLocation = new SocialLocation(getPlayer().getLocation().toVector(), getPlayer().getLocation().getWorld().getName(),
                    "Brand new location", new PublicAccess());

            // Opening the menu to edit the new location
            setMenuToOpen(new LocationEditorMenu(getPlayer(), newLocation, false));

            super.clicked(click);
        }
    }
}
