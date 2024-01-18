package ulisboa.tecnico.minesocieties.utils;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class StringUtils {

    public static String toBirthdayString(Instant birthday) {
        return DateTimeFormatter.ISO_LOCAL_DATE.format(birthday.atOffset(ZoneOffset.UTC));
    }

    public static String toBirthdayString(OffsetDateTime birthday) {
        return DateTimeFormatter.ISO_LOCAL_DATE.format(birthday);
    }
}
