package ulisboa.tecnico.minesocieties.guis;

import org.bukkit.Material;

public class EmptyPaneDecoration extends GUIDecoration {
    public EmptyPaneDecoration(GUIMenu menu) {
        super(menu, Material.WHITE_STAINED_GLASS_PANE, " ");
    }

    public EmptyPaneDecoration(GUIMenu menu, Material paneMaterial) {
        super(menu, paneMaterial, " ");
    }
}
