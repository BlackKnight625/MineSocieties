package ulisboa.tecnico.minesocieties.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

/**
 *  Utils class for formatting messages to be sent to players or to be broadcasted
 */
public class ComponentUtils {

    // Private attributes

    private static final Component SOCIETIES_PREFIX = Component.empty()
            .append(Component.text("[", TextColor.color(71, 151, 51)))
            .append(Component.text("MS", TextColor.color(122, 122, 122)))
            .append(Component.text("] ", TextColor.color(71, 151, 51)));

    // Other methods

    public static Component withPrefix(Component other) {
        return SOCIETIES_PREFIX.append(other);
    }
}
