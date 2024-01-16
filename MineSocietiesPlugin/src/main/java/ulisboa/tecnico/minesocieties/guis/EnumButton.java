package ulisboa.tecnico.minesocieties.guis;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *  Button for changing the amounts of something
 *
 * @param <T> The type of the number being manipulated. Can either be Integer or Double. Otherwise,
 *           errors will be thrown.
 */
public class EnumButton<T extends Enum<T>> extends GUIItem {

    // Private attributes

    private final Consumer<T> whenClicked;
    private final Function<T, Material> enumMaterialsProvider;
    private T currentEnum;
    private final T[] allEnums;

    // Constructors

    public EnumButton(GUIMenu menu, String name, T currentEnum,
                      Consumer<T> whenClicked, Function<T, Material> enumMaterialsProvider) {
        super(menu, Material.BARRIER, name);

        this.whenClicked = whenClicked;
        this.enumMaterialsProvider = enumMaterialsProvider;
        this.currentEnum = currentEnum;

        allEnums = (T[]) currentEnum.getClass().getEnumConstants();

        update();
    }

    public EnumButton(GUIMenu menu, String name, T currentEnum,
                      Consumer<T> whenClicked, Material... enumMaterials) {
        this(menu, name, currentEnum, whenClicked, e -> e.ordinal() < enumMaterials.length ? enumMaterials[e.ordinal()] : null);
    }

    public EnumButton(GUIMenu menu, String name, T currentEnum,
                      Consumer<T> whenClicked, Map<T, Material> enumToMaterial) {
        this(menu, name, currentEnum, whenClicked, enumToMaterial::get);
    }

    // Other methods

    private T nextEnum() {
        int nextOrdinal = (currentEnum.ordinal() + 1) % allEnums.length;

        return allEnums[nextOrdinal];
    }

    private Material getNewMaterial() {
        Material material = enumMaterialsProvider.apply(currentEnum);

        return material == null ? getDefaultMaterial() : material;
    }

    public Material getDefaultMaterial() {
        return Material.GRASS_BLOCK;
    }

    public void update() {
        getItemStack().setType(getNewMaterial());

        clearDescription();

        addDescription(ChatColor.BLUE, "Current option: ");
        addDescription(ChatColor.DARK_GREEN, currentEnum.toString());
        addDescription(ChatColor.GRAY, "-----------------");
        addDescription(ChatColor.BLUE, "Click to select next option: ");
        addDescription(ChatColor.AQUA, nextEnum().toString());
    }

    @Override
    public void clicked(ClickType click) {
        currentEnum = nextEnum();

        whenClicked.accept(currentEnum);

        update();

        getMenu().update();
    }
}
