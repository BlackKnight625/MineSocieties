package ulisboa.tecnico.minesocieties.guis.social.locations;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.location.LocationAccessType;
import ulisboa.tecnico.minesocieties.agents.location.SocialLocation;
import ulisboa.tecnico.minesocieties.agents.npc.state.CharacterReference;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.GUIItem;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenu;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenuOpener;
import ulisboa.tecnico.minesocieties.guis.common.GoBack;

public class AccessEditMenu extends GUIMenu {

    // Private attributes

    private final SocialLocation location;
    private final boolean editingIsLimited;

    // Constructors

    public AccessEditMenu(SocialPlayer player, SocialLocation location, boolean editingIsLimited) {
        super(player, "Access Settings", 27);

        this.location = location;
        this.editingIsLimited = editingIsLimited;
    }

    // Other methods

    @Override
    public void fillShopWithClickables() {
        if (!editingIsLimited) {
            addClickable(11, new AccessChangerItem(LocationAccessType.PUBLIC));
            addClickable(12, new AccessChangerItem(LocationAccessType.SHARED));
            addClickable(13, new AccessChangerItem(LocationAccessType.PERSONAL));
        }

        AgentSelectionMenu selectionMenu = location.getAccess().getAccessAgentsEditor(getPlayer(), location);

        if (selectionMenu != null) {
            GUIMenuOpener agentsSelector = new GUIMenuOpener(this, Material.REPEATER, selectionMenu, ChatColor.BLUE + "Edit NPCs with access");

            agentsSelector.addDescription(ChatColor.AQUA, "NPCs with access");

            // Adding the names of all agents with explicit access
            agentsSelector.addDescription(ChatColor.GRAY, location.getAccess().getAgentsWithAccess().stream().map(CharacterReference::getName).toList());

            addClickable(15, agentsSelector);
        }

        addClickable(26, new GoBack(this));

        fillRestWithPanes(Material.YELLOW_STAINED_GLASS_PANE);
    }

    // Private classes

    private class AccessChangerItem extends GUIItem {

        // Private attributes

        private final LocationAccessType type;

        // Constructors

        public AccessChangerItem(LocationAccessType type) {
            super(AccessEditMenu.this, type.getGuiMaterial(), ChatColor.YELLOW + type.getGuiName());

            this.type = type;

            addDescription(ChatColor.GRAY, type.getGuiDescription());

            if (type == location.getAccess().getType()) {
                makeItemGlow();
                addDescription(ChatColor.GREEN, "Currently selected");
            } else {
                addDescription(ChatColor.RED, "Click to change");
            }
        }

        // Other methods

        @Override
        public void clicked(ClickType click) {
            if (type != location.getAccess().getType()) {
                location.setAccess(type.createInstance());

                getPlayer().getPlayer().playSound(getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

                MineSocieties.getPlugin().getLocationsManager().saveAsync(location);

                hardReset();
            }
        }
    }
}
