package ulisboa.tecnico.minesocieties.guis.social.locations;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.Nullable;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.location.PersonalAccess;
import ulisboa.tecnico.minesocieties.agents.location.SocialLocation;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.CharacterReference;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;

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
        getAgents().removeIf(location::isSpecialLocation);
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
        boolean success = false;

        if (type.isLeftClick() && !agent.toReference().equals(reference)) {
            if (reference != null) {
                // Must make the previous agent forget the location
                access.forgetLocation(location.toReference(), (SocialAgent) reference.getReferencedCharacter());
            }

            // Set the agent
            access.setCharacter(agent.toReference());
            access.rememberLocation(location.toReference(), agent);

            success = true;
        } else if (type.isRightClick() && agent.toReference().equals(reference)) {
            // Remove the agent
            access.setCharacter(null);
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
    public void customModifyAgentSelector(AgentSelectorItem agentSelectorItem) {
        agentSelectorItem.addDescription(""); // New line

        if (reference != null && reference.equals(agentSelectorItem.getAgent().toReference())) {
            agentSelectorItem.makeItemGlow();

            agentSelectorItem.addDescription(ChatColor.GREEN, "Selected!");
            agentSelectorItem.addDescription(ChatColor.RED, "Right-click to remove");
        } else {
            agentSelectorItem.addDescription(ChatColor.GREEN + "Left-click to select");
        }
    }
}
