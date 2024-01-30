package ulisboa.tecnico.minesocieties.guis.social.information.states;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.guis.common.GUIDecoration;
import ulisboa.tecnico.minesocieties.guis.common.GUIMenu;

public class InformativeItem extends GUIDecoration {

    // Constructors

    public InformativeItem(GUIMenu menu) {
        super(menu, Material.BEACON, ChatColor.BLUE + "Info");

        addDescription(ChatColor.GRAY,
                "NPC states are a single word",
                "which describe such state.",
                "They can also be hyphenated words,",
                "such as chocolate-lover, which",
                "could be used to describe more",
                "specific states such as really",
                "liking chocolate.",
                "",
                "When adding new states, keep",
                "in mind that this is not supposed",
                "to serve as an NPC's memory.",
                "States are very volatile as they",
                "change very often and have a direct",
                "impact on the NPC's behaviour.");
    }
}
