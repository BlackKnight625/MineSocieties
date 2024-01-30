package ulisboa.tecnico.minesocieties.guis.social.information.memory;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.guis.common.GUIDecoration;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenu;

public class InformativeItem extends GUIDecoration {

    // Constructors

    public InformativeItem(GUIMenu menu) {
        super(menu, Material.BEACON, ChatColor.BLUE + "Info");

        addDescription(ChatColor.GRAY,
                "NPC memories are natural ",
                "language sentences. If you want to",
                "create a new memory, make sure it's",
                "a single sentence. Any written ",
                "references to this agent should",
                "use its full name. Preferably,",
                "use English.",
                "",
                "Memories change over time, so,",
                "something you write might not be",
                "permanent.");
    }
}
