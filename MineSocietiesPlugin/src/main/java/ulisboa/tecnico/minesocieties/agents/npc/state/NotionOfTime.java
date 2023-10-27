package ulisboa.tecnico.minesocieties.agents.npc.state;

import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum NotionOfTime {
    VERY_RECENTLY(instant -> instant.minus(10, ChronoUnit.SECONDS), "very recently"),
    RECENTLY(instant -> instant.minus(10, ChronoUnit.MINUTES), "recently"),
    NOT_LONG_AGO(instant -> instant.minus(1, ChronoUnit.HOURS), "not long ago"),
    A_FEW_HOURS_AGO(instant -> instant.minus(5, ChronoUnit.HOURS), "a few hours ago"),
    A_FEW_DAYS_AGO(instant -> instant.minus(5, ChronoUnit.DAYS), "a few days ago"),
    A_LONG_TIME_AGO(instant -> instant.minus(100, ChronoUnit.DAYS), "a long time ago"),
    AGES_AGO(instant -> instant.minus(5, ChronoUnit.YEARS), "ages ago");

    // Private attributes

    private final Function<Instant, Instant> timeManipulator;
    private final String explanation;

    private static final Map<String, NotionOfTime> EXPLANATION_TO_NOTION = new HashMap<>();

    // Constructors

    NotionOfTime(Function<Instant, Instant> timeManipulator, String explanation) {
        this.timeManipulator = timeManipulator;
        this.explanation = explanation;
    }

    // Getters and setters

    public String getExplanation() {
        return explanation;
    }

    // Other methods

    public Instant subtract(Instant instant) {
        return timeManipulator.apply(instant);
    }

    public static @Nullable NotionOfTime fromExplanation(String explanation) {
        return EXPLANATION_TO_NOTION.get(explanation);
    }

    // Static

    static {
        for (NotionOfTime notionOfTime : NotionOfTime.values()) {
            EXPLANATION_TO_NOTION.put(notionOfTime.explanation, notionOfTime);
        }
    }
}
