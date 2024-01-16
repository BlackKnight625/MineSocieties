package ulisboa.tecnico.minesocieties.guis.common;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ToggleButton extends GUIItem {

    // Private attributes
    private final Supplier<Boolean> currentValue;
    private final Consumer<Boolean> whenToggled;
    private final Material trueMaterial;
    private final Material falseMaterial;
    private final String prefix;


    // Constructors

    public ToggleButton(GUIMenu menu, String prefix, Supplier<Boolean> currentValue,
                        Consumer<Boolean> whenToggled, Material trueMaterial, Material falseMaterial) {
        super(menu, Material.BARRIER, "");

        this.currentValue = currentValue;
        this.whenToggled = whenToggled;
        this.trueMaterial = trueMaterial;
        this.falseMaterial = falseMaterial;
        this.prefix = prefix;

        updateMaterial();

        addDescription(ChatColor.AQUA, "Click to toggle");
    }


    // Other methods

    public void updateMaterial() {
        getItemStack().setType(currentValue.get() ? trueMaterial : falseMaterial);

        ItemMeta meta = getItemStack().getItemMeta();
        meta.setDisplayName(prefix + " " + (currentValue.get() ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
        getItemStack().setItemMeta(meta);
    }

    @Override
    public void clicked(ClickType click) {
        whenToggled.accept(!currentValue.get());

        updateMaterial();

        getMenu().update();
    }
}
