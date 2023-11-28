package ulisboa.tecnico.minesocieties.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

/**
 *  Utils class for formatting messages to be sent to players or to be broadcasted
 */
public class ComponentUtils {

    // Private attributes

    private static final TextColor BRACKET_COLOR = TextColor.color(71, 151, 51);

    private static final Component SOCIETIES_PREFIX = Component.empty()
            .append(Component.text("[", BRACKET_COLOR))
            .append(Component.text("MS", TextColor.color(122, 122, 122)))
            .append(Component.text("] ", BRACKET_COLOR));
    private static final Component SPEECH_BUBBLE = Component.text("\uD83D\uDCAC", TextColor.color(255, 255, 255));

    // Other methods

    public static Component withPrefix(Component other) {
        return SOCIETIES_PREFIX.append(other);
    }

    public static Component speechBubbleTo(String toWhom, String message) {
        return Component.empty()
                .append(Component.text("[", BRACKET_COLOR))
                .append(SPEECH_BUBBLE)
                .append(Component.text(" -> ").color(TextColor.color(122, 122, 122)))
                .append(Component.text(toWhom).color(TextColor.color(226, 195, 15)))
                .append(Component.text("] ", BRACKET_COLOR))
                .append(Component.text(message));
    }

    public static Component sendMessageToPrefix(String from, String to, String message) {
        return Component.empty()
                .append(Component.text("[", BRACKET_COLOR))
                .append(SPEECH_BUBBLE)
                .append(Component.text(" " + from).color(TextColor.color(226, 195, 15)))
                .append(Component.text(" -> ").color(TextColor.color(122, 122, 122)))
                .append(Component.text(to).color(TextColor.color(226, 195, 15)))
                .append(Component.text("] ", BRACKET_COLOR))
                .append(Component.text(message));
    }
}
