package ulisboa.tecnico.minesocieties.agents.npc.state;

import java.time.Instant;

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
}
