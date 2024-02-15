package ulisboa.tecnico.minesocieties.agents.npc.state;

import java.time.Instant;
import java.util.Objects;

public class NotionOfEvent extends InstantMemory {

    // Private attributes

    private final String eventDescription;

    // Constructors

    public NotionOfEvent(Instant instant, String eventDescription) {
        super(instant);

        this.eventDescription = eventDescription;
    }

    // Getters and setters

    public String getEventDescription() {
        return eventDescription;
    }

    // Other methods

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotionOfEvent that = (NotionOfEvent) o;
        return eventDescription.equals(that.eventDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventDescription);
    }
}
