package ulisboa.tecnico.minesocieties.guis.social.locations;

import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.Nullable;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.location.PersonalAccess;
import ulisboa.tecnico.minesocieties.agents.location.SocialLocation;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.CharacterReference;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;

import java.util.function.Predicate;

public class PersonalAccessAgentsEditorMenu extends AgentSelectionMenu {

    // Private attributes

    private final PersonalAccess access;
    private final SocialLocation location;
    private @Nullable CharacterReference reference;

    // Constructors

    public PersonalAccessAgentsEditorMenu(SocialPlayer player, SocialLocation location, PersonalAccess access) {
        super(player, "Select the sole agent with access");

        this.location = location;
        this.access = access;

        // Not allowing agents to be removed if this location is their home
        getAgents().removeIf(location::isAgentsHome);
    }

    // Other methods

    @Override
    public void fillShopWithClickables() {
        // Caching the already selected agents
        reference = access.getCharacter();

        super.fillShopWithClickables();
    }

    @Override
    public void onAgentSelected(SocialAgent agent, ClickType type) {
        if (type.isLeftClick()) {
            // Set the agent
            access.setCharacter(agent.toReference());
        } else {
            // Remove the agent
            access.setCharacter(null);

            agent.deleteAgentsInvalidLocations();
        }

        MineSocieties.getPlugin().getLocationsManager().saveAsync(location);

        getPlayer().getPlayer().playSound(getPlayer().getPlayer().getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

        hardReset();
    }

    @Override
    public void customModifyAgentSelector(AgentSelectorItem agentSelectorItem) {
        if (reference != null && reference.equals(agentSelectorItem.getAgent().toReference())) {
            agentSelectorItem.makeItemGlow();

            agentSelectorItem.addDescription(""); // New line
            agentSelectorItem.addDescription(ChatColor.GREEN, "Selected!");
            agentSelectorItem.addDescription(ChatColor.RED, "Right-click to remove");
        } else {
            agentSelectorItem.addDescription(ChatColor.GREEN + "Left-click to select");
        }
    }
}
