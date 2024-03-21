package ulisboa.tecnico.minesocieties.guis.social.actions;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import ulisboa.tecnico.minesocieties.agents.actions.ISocialAction;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.guis.common.ErrorMenu;
import ulisboa.tecnico.minesocieties.guis.common.GUIItem;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenu;
import ulisboa.tecnico.minesocieties.utils.StringUtils;
import ulisboa.tecnico.minesocieties.visitors.IActionVisitor;
import ulisboa.tecnico.minesocieties.visitors.NextActionExplainer;

public class ActionExecutorItem extends GUIItem {

    // Private attributes

    private final ISocialAction action;
    private final SocialAgent agent;

    private static final IActionVisitor EXPLAINER = new NextActionExplainer();

    // Constructors

    public ActionExecutorItem(GUIMenu menu, Material material, SocialAgent agent, ISocialAction action) {
        super(menu, material, ChatColor.YELLOW + "Execute the action:");

        addDescription(ChatColor.GRAY, StringUtils.splitIntoLines(action.accept(EXPLAINER), 30));

        this.agent = agent;
        this.action = action;
    }

    // Other methods

    @Override
    public void clicked(ClickType click) {
        if (action.canBeExecuted(agent)) {
            agent.selectedNewActionSync(action);
        } else {
            new ErrorMenu(getMenu().getPlayer(), "This action cannot be executed", getMenu()).open();
        }
    }
}
