package ulisboa.tecnico.minesocieties.guis.social.locations;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.GUIItem;
import ulisboa.tecnico.minesocieties.guis.common.GoBack;
import ulisboa.tecnico.minesocieties.guis.common.PageableMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public abstract class AgentSelectionMenu extends PageableMenu {

    // Private attributes

    private final List<SocialAgent> agents = new ArrayList<>();

    private static final int MAX_AGENTS_PER_PAGE = 45;

    // Constructors

    public AgentSelectionMenu(SocialPlayer player, String name) {
        super(player, name, 54);

        // Adding all agents
        MineSocieties.getPlugin().getSocialAgentManager().forEachAgent(agents::add);
    }

    // Getters and setters

    protected List<SocialAgent> getAgents() {
        return agents;
    }

    // Other methods

    public void reloadItemsInPage() {
        fillPageFromList(0, MAX_AGENTS_PER_PAGE, agents, AgentSelectorItem::new);

        addClickable(53, new GoBack(this));

        if (agents.size() > MAX_AGENTS_PER_PAGE) {
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
    public void fillShopWithClickables() {
        reloadItemsInPage();
    }

    @Override
    public int getMaxPages() {
        return (agents.size() / MAX_AGENTS_PER_PAGE) + 1;
    }

    public abstract void onAgentSelected(SocialAgent agent, ClickType type);

    public void customModifyAgentSelector(AgentSelectorItem agentSelectorItem) {
        // Only shows the "Click to select" by default
        agentSelectorItem.addDescription(ChatColor.GREEN + "Click to select");
    }

    // Private classes

    protected class AgentSelectorItem extends GUIItem {

        // Private attributes

        private final SocialAgent agent;

        // Constructors

        public AgentSelectorItem(SocialAgent agent) {
            super(AgentSelectionMenu.this, Material.PLAYER_HEAD, ChatColor.GOLD + "NPC: " + agent.getName());

            this.agent = agent;

            // Making the skull look like the agent
            SkullMeta skullMeta = (SkullMeta) getItemStack().getItemMeta();

            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(agent.getSkin()));

            getItemStack().setItemMeta(skullMeta);

            customModifyAgentSelector(this);
        }

        // Getters and setters

        public SocialAgent getAgent() {
            return agent;
        }

        // Other methods

        @Override
        public void clicked(ClickType click) {
            onAgentSelected(agent, click);
        }
    }
}
