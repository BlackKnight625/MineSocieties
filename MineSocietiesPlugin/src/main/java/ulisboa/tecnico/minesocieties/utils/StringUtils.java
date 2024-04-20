package ulisboa.tecnico.minesocieties.utils;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    public static String toBirthdayString(Instant birthday) {
        return DateTimeFormatter.ISO_LOCAL_DATE.format(birthday.atOffset(ZoneOffset.UTC));
    }

    public static String toBirthdayString(OffsetDateTime birthday) {
        return DateTimeFormatter.ISO_LOCAL_DATE.format(birthday);
    }

    /**
     *  Splits the given original String into an array of Strings that represents the original String if it were
     * written in multiple lines, given the maxLineLength. If the last line's word makes the line's length longer
     * than maxLineLength, it's still included.
     * @param maxLineLength
     *  The max line length
     * @return
     *  The original String split into several lines
     */
    public static List<String> splitIntoLines(String original, int maxLineLength) {
        assert maxLineLength >= 1 : "Given max line length must be greater than 1! It's " + maxLineLength;

        StringBuilder lineBuilder = new StringBuilder();
        List<String> result = new ArrayList<>();

        for (int i = 0; i < original.length(); i++) {
            char letter = original.charAt(i);
            boolean addedLine = false;

            if (Character.isWhitespace(letter) && lineBuilder.length() >= maxLineLength) {
                // Reached a whitespace and there's no more space for the line
                String line = lineBuilder.toString();
                result.add(line);

                lineBuilder = new StringBuilder();

                if (i + 1 < original.length()) {
                    // There's at least 1 more character, meaning that there'll be another line
                    // Appending the last colors from the last line
                    lineBuilder.append(ChatColor.getLastColors(line));
                }

                addedLine = true;
            } else {
                lineBuilder.append(letter);
            }

            if (i + 1 == original.length() && !addedLine) {
                // This is the last character and a line has not been added
                result.add(lineBuilder.toString());
            }
        }

        return result;
    }

    public static String itemToAmountAndName(ItemStack item) {
        return item.getAmount() + " " + item.getType().toString().replace('_', ' ').toLowerCase();
    }
}
