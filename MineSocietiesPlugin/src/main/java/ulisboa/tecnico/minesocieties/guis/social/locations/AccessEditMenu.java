package ulisboa.tecnico.minesocieties.guis.social.locations;

import ulisboa.tecnico.minesocieties.agents.location.SocialLocation;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenu;

public class AccessEditMenu extends GUIMenu {

    // Private attributes

    private final SocialLocation location;

    // Constructors

    public AccessEditMenu(SocialPlayer player, SocialLocation location) {
        super(player, "Access Settings", 27);

        this.location = location;
    }

    // Other methods

    @Override
    public void fillShopWithClickables() {

    }
}
