package ulisboa.tecnico.minesocieties.guis.social.information.locations;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.SkullMeta;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.common.GUIItem;
import ulisboa.tecnico.minesocieties.guis.common.GoBack;
import ulisboa.tecnico.minesocieties.guis.common.PageableMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class AgentSelectionMenu extends PageableMenu {

    // Private attributes

    private final List<SocialAgent> agents = new ArrayList<>();
    private final BiConsumer<SocialAgent, ClickType> whenSelected;

    private static final int MAX_AGENTS_PER_PAGE = 45;

    // Constructors

    public AgentSelectionMenu(SocialPlayer player, String name, BiConsumer<SocialAgent, ClickType> whenSelected) {
        super(player, name, 54);

        this.whenSelected = whenSelected;

        // Adding all agents
        MineSocieties.getPlugin().getSocialAgentManager().forEachAgent(agents::add);
    }

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

    // Private classes

    private class AgentSelectorItem extends GUIItem {

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

            addDescription(ChatColor.GREEN + "Click to select");
        }

        // Other methods

        @Override
        public void clicked(ClickType click) {
            whenSelected.accept(agent, click);
        }
    }
}
