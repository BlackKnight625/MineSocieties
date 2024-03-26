package ulisboa.tecnico.minesocieties.guis.social.locations;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
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
    private Collection<CharacterReference> agentsWithAccess;

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
        agentsWithAccess = access.getAgentsWithAccess();

        super.fillShopWithClickables();
    }

    @Override
    public void onAgentSelected(SocialAgent agent, ClickType type) {
        boolean success = false;

        if (type.isLeftClick() && !agentsWithAccess.contains(agent.toReference())) {
            // Adding the agent to the list
            access.addAgent(agent.toReference());
            access.rememberLocation(location.toReference(), agent);

            success = true;
        } else if (type.isRightClick() && agentsWithAccess.contains(agent.toReference())) {
            // Removing the agent from the list
            access.removeAgent(agent.toReference());
            access.forgetLocation(location.toReference(), agent);

            success = true;
        }

        if (success) {
            MineSocieties.getPlugin().getLocationsManager().saveAsync(location);

            getPlayer().getPlayer().playSound(
                    getPlayer().getPlayer().getLocation(),
                    type.isLeftClick() ? Sound.BLOCK_NOTE_BLOCK_PLING : Sound.BLOCK_LAVA_EXTINGUISH,
                    1,
                    1);

            hardReset();
        }
    }

    @Override
    public void customModifyAgentSelector(AgentSelectionMenu.AgentSelectorItem agentSelectorItem) {
        agentSelectorItem.addDescription(""); // New line

        if (agentsWithAccess.contains(agentSelectorItem.getAgent().toReference())) {
            agentSelectorItem.makeItemGlow();

            agentSelectorItem.addDescription(ChatColor.GREEN, "Selected!");
            agentSelectorItem.addDescription(ChatColor.RED, "Right-click to remove");
        } else {
            agentSelectorItem.addDescription(ChatColor.GREEN + "Left-click to select");
        }
    }
}
