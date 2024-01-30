package ulisboa.tecnico.minesocieties.guis.social.information;

import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenu;
import ulisboa.tecnico.minesocieties.guis.common.GoBack;
import ulisboa.tecnico.minesocieties.guis.social.information.locations.LocationsItem;
import ulisboa.tecnico.minesocieties.guis.social.information.memory.LongTermMemoryItem;
import ulisboa.tecnico.minesocieties.guis.social.information.memory.PastActionsItem;
import ulisboa.tecnico.minesocieties.guis.social.information.memory.ShortTermMemoryItem;
import ulisboa.tecnico.minesocieties.guis.social.information.states.EmotionsItem;
import ulisboa.tecnico.minesocieties.guis.social.information.states.PersonalitiesItem;

public class AgentInformationMenu extends GUIMenu {

    // Private attributes

    private final SocialAgent agent;

    // Constructors

    public AgentInformationMenu(SocialPlayer player, SocialAgent agent) {
        super(player, agent.getName() + "'s information", 45);

        this.agent = agent;
    }

    // Other methods

    @Override
    public void fillShopWithClickables() {
        addClickable(10, new NameItem(this, agent));
        addClickable(11, new UUIDItem(this, agent));
        addClickable(12, new AgeItem(this, agent));

        addClickable(16, new LocationsItem(this, agent));

        addClickable(28, new PersonalitiesItem(this, agent));
        addClickable(29, new EmotionsItem(this, agent));

        addClickable(32, new PastActionsItem(this, agent));
        addClickable(33, new ShortTermMemoryItem(this, agent));
        addClickable(34, new LongTermMemoryItem(this, agent));

        addClickable(44, new GoBack(this));

        fillRestWithPanes(Material.PURPLE_STAINED_GLASS_PANE);
    }
}
