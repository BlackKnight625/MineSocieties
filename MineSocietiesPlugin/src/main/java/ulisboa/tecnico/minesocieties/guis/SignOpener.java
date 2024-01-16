package ulisboa.tecnico.minesocieties.guis;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class SignOpener extends GUIItem {

    // Private attributes

    private final Consumer<List<String>> callback;

    // Constructors

    public SignOpener(GUIMenu menu, Material material, String name, Consumer<List<String>> callback) {
        super(menu, material, name);

        this.callback = callback;
    }

    public SignOpener(GUIMenu menu, ItemStack item, String name, Consumer<List<String>> callback) {
        super(menu, item, name);

        this.callback = callback;
    }

    public SignOpener(GUIMenu menu, ItemStack item, Consumer<List<String>> callback) {
        super(menu, item);

        this.callback = callback;
    }

    // Other methods

    @Override
    public void clicked(ClickType click) {
        //MineSocieties.getPlugin().getGuiManager().openSignGUI(getShopper(), callback);
    }
}
