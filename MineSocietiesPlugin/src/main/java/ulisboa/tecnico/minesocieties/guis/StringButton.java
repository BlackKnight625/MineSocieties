package ulisboa.tecnico.minesocieties.guis;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *  Button for changing a String option from a selection of options
 */
public class StringButton extends GUIItem {

    // Private attributes

    private final Consumer<String> whenClicked;
    private final Function<Integer, Material> enumMaterialsProvider;
    private int currentIndex;
    private final List<String> allOptions;

    // Constructors

    public StringButton(GUIMenu menu, String name, int currentIndex, List<String> allOptions,
                        Consumer<String> whenClicked, Function<Integer, Material> enumMaterialsProvider) {
        super(menu, Material.BARRIER, name);

        this.whenClicked = whenClicked;
        this.enumMaterialsProvider = enumMaterialsProvider;
        this.currentIndex = currentIndex;

        this.allOptions = allOptions;

        update();
    }

    public StringButton(GUIMenu menu, String name, int currentIndex, List<String> allOptions,
                        Consumer<String> whenClicked, Material... indexMaterials) {
        this(menu, name, currentIndex, allOptions, whenClicked, i -> i < indexMaterials.length ? indexMaterials[i] : null);
    }

    public StringButton(GUIMenu menu, String name, int currentIndex, List<String> allOptions,
                        Consumer<String> whenClicked, Map<Integer, Material> enumToMaterial) {
        this(menu, name, currentIndex, allOptions, whenClicked, enumToMaterial::get);
    }

    // Other methods

    protected String nextOption() {
        return allOptions.get(nextIndex());
    }

    private int nextIndex() {
        return (currentIndex + 1) % allOptions.size();
    }

    protected String currentOption() {
        return allOptions.get(currentIndex);
    }

    private Material getNewMaterial() {
        Material material = enumMaterialsProvider.apply(currentIndex);

        return material == null ? getDefaultMaterial() : material;
    }

    public Material getDefaultMaterial() {
        return Material.GRASS_BLOCK;
    }

    public void update() {
        getItemStack().setType(getNewMaterial());

        clearDescription();

        addDescription(ChatColor.BLUE, "Current option: ");
        addDescription(ChatColor.DARK_GREEN, formatOption(currentOption()));
        addDescription(ChatColor.GRAY, "-----------------");
        addDescription(ChatColor.BLUE, "Click to select next option: ");
        addDescription(ChatColor.AQUA, formatOption(nextOption()));
    }

    public String formatOption(String option) {
        return option;
    }

    @Override
    public void clicked(ClickType click) {
        currentIndex = nextIndex();

        whenClicked.accept(currentOption());

        update();

        getMenu().update();
    }
}
