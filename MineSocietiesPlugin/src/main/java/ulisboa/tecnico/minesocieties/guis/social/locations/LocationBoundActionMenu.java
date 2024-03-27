package ulisboa.tecnico.minesocieties.guis.social.locations;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.location.LocationBoundActionType;
import ulisboa.tecnico.minesocieties.agents.location.SocialLocation;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.ErrorMenu;
import ulisboa.tecnico.minesocieties.guis.common.GUIItem;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenu;
import ulisboa.tecnico.minesocieties.guis.common.GoBack;

public class LocationBoundActionMenu extends GUIMenu {

    // Private attributes

    private final SocialLocation location;

    // Constructors

    public LocationBoundActionMenu(SocialPlayer player, SocialLocation location) {
        super(player, "Choose actions", 36);

        this.location = location;
    }

    // Other methods

    @Override
    public void fillShopWithClickables() {
        int i = 0;

        // Adding the actions in a pretty way
        for (LocationBoundActionType actionType : LocationBoundActionType.values()) {
            addClickable((i % 7) + 9 * (i / 7) + 10, new ActionSelector(actionType));

            i++;
        }

        addClickable(35, new GoBack(this));

        fillRestWithPanes(Material.GRAY_STAINED_GLASS_PANE);
    }

    // Private classes

    private class ActionSelector extends GUIItem {

        // Private attributes

        private final LocationBoundActionType actionType;
        private final boolean isSelected;

        // Constructors

        public ActionSelector(LocationBoundActionType actionType) {
            super(LocationBoundActionMenu.this, actionType.getGuiMaterial(), ChatColor.BLUE + actionType.getGuiName());

            this.actionType = actionType;

            isSelected = location.hasPossibleAction(actionType);

            addDescription("");

            if (isSelected) {
                addDescription(ChatColor.GREEN, "Selected");
                addDescription(ChatColor.RED, "Right-click to remove");

                makeItemGlow();
            } else {
                addDescription(ChatColor.RED, "Not selected");
                addDescription(ChatColor.GREEN, "Left-click to add");
            }
        }

        // Other methods

        @Override
        public void clicked(ClickType click) {
            if (click.isLeftClick() && !isSelected) {
                // Checking if the action can be added
                var pair = actionType.canBeExecutedInLocation(location.toBukkitLocation());

                if (pair.getLeft()) {
                    // The action can be executed at the location. Adding it
                    location.addPossibleAction(actionType);

                    MineSocieties.getPlugin().getLocationsManager().saveAsync(location);

                    getPlayer().getPlayer().playSound(getPlayer().getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

                    hardReset();
                } else {
                    // The action cannot be executed at the location. Showing the error
                    new ErrorMenu(getPlayer(), pair.getRight(), LocationBoundActionMenu.this).open();
                }
            } else if (click.isRightClick() && isSelected) {
                // Removing the action
                location.removePossibleAction(actionType);

                MineSocieties.getPlugin().getLocationsManager().saveAsync(location);

                getPlayer().getPlayer().playSound(getPlayer().getPlayer().getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);

                hardReset();
            }
        }
    }
}
