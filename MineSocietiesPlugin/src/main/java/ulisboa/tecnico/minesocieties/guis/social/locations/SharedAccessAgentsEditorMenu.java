package ulisboa.tecnico.minesocieties.guis.social.locations;

import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.location.SharedAccess;
import ulisboa.tecnico.minesocieties.agents.location.SocialLocation;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.CharacterReference;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;

import java.util.Collection;

public class SharedAccessAgentsEditorMenu extends AgentSelectionMenu {

    // Private attributes

    private final SharedAccess access;
    private final SocialLocation location;
    private Collection<CharacterReference> stronglyConnectedAgents;

    // Constructors

    public SharedAccessAgentsEditorMenu(SocialPlayer player, SocialLocation location, SharedAccess access) {
        super(player, "Select those who can see this location");

        this.location = location;
        this.access = access;

        // Not allowing agents to be removed if this location is their home
        getAgents().removeIf(location::isAgentsHome);
    }

    // Other methods


    @Override
    public void fillShopWithClickables() {
        // Caching the already selected agents
        stronglyConnectedAgents = access.getStronglyConnectedAgents();

        super.fillShopWithClickables();
    }

    @Override
    public void onAgentSelected(SocialAgent agent, ClickType type) {
        if (type.isLeftClick()) {
            // Adding the agent to the list
            access.addAgent(agent.toReference());
        } else {
            // Removing the agent from the list
            access.removeAgent(agent.toReference());

            agent.deleteAgentsInvalidLocations();
        }

        MineSocieties.getPlugin().getLocationsManager().saveAsync(location);

        getPlayer().getPlayer().playSound(getPlayer().getPlayer().getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

        hardReset();
    }

    @Override
    public void customModifyAgentSelector(AgentSelectionMenu.AgentSelectorItem agentSelectorItem) {
        if (stronglyConnectedAgents.contains(agentSelectorItem.getAgent().toReference())) {
            agentSelectorItem.makeItemGlow();

            agentSelectorItem.addDescription(""); // New line
            agentSelectorItem.addDescription(ChatColor.GREEN, "Selected!");
            agentSelectorItem.addDescription(ChatColor.RED, "Right-click to remove");
        } else {
            agentSelectorItem.addDescription(ChatColor.GREEN + "Left-click to select");
        }
    }
}
