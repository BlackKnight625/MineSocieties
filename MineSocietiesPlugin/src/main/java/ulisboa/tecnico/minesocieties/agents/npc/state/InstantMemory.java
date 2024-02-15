package ulisboa.tecnico.minesocieties.agents.npc.state;

import java.time.Duration;
import java.time.Instant;

public class InstantMemory {

    // Private attributes

    private final Instant instant;

    // Constructors

    public InstantMemory(Instant instant) {
        this.instant = instant;
    }

    // Getters and setters

    public Instant getInstant() {
        return instant;
    }

    // Other methods

    public long getSecondsHowLongAgo() {
        return Duration.between(this.instant, Instant.now()).getSeconds();
    }

    public String getApproximateHowLongAgo() {
        long totalSeconds = getSecondsHowLongAgo();

        if (totalSeconds < 5 * 60) {
            return "very recently";
        } else if (totalSeconds < 30 * 60L) {
            return "recently";
        } else if (totalSeconds < 2 * 60 * 60L) {
            return "a while ago";
        } else if (totalSeconds < 10 * 60 * 60L) {
            return "a few hours ago";
        } else if (totalSeconds < 24 * 60 * 60L) {
            return "some hours ago";
        } else if (totalSeconds < 7 * 24 * 60 * 60L) {
            return "a few days ago";
        } else if (totalSeconds < 30 * 24 * 60 * 60L) {
            return "many days ago";
        } else if (totalSeconds < 3 * 30 * 24 * 60 * 60L) {
            return "a few months ago";
        } else if (totalSeconds < 12 * 30 * 24 * 60 * 60L) {
            return "many months ago";
        } else {
            return "a long time ago";
        }
    }

    public String getExactHowLongAgo() {
        StringBuilder builder = new StringBuilder();
        long secondsAgo = getSecondsHowLongAgo();
        long minutesAgo = secondsAgo / 60L;
        long hoursAgo = minutesAgo / 60L;
        long daysAgo = hoursAgo / 24L;
        long weeksAgo = daysAgo / 7L;
        long monthsAgo = daysAgo / 30L;
        long yearsAgo = daysAgo / 365L;

        // Explaining how long ago the something took place
        if (yearsAgo > 0) {
            // Happened a very long time ago
            builder.append(yearsAgo).append(yearsAgo == 1 ? " year " : " years ").append("ago");
        } else if (monthsAgo > 0) {
            // Happened long ago
            builder.append(monthsAgo).append(monthsAgo == 1 ? " month " : " months ").append("ago");
        } else if (weeksAgo > 0) {
            // Happened a while ago
            builder.append(weeksAgo).append(weeksAgo == 1 ? " week " : " weeks ").append("ago");
        } else if (hoursAgo > 0) {
            // Happened a few hours ago
            builder.append(hoursAgo).append(hoursAgo == 1 ? " hour " : " hours ").append("ago");
        } else if (minutesAgo > 0) {
            // Was recent
            builder.append(minutesAgo).append(minutesAgo == 1 ? " minute " : " minutes ").append("ago");
        } else {
            // Was very recent
            builder.append(secondsAgo).append(secondsAgo == 1 ? " second " : " seconds ").append("ago");
        }

        return builder.toString();
    }
}
