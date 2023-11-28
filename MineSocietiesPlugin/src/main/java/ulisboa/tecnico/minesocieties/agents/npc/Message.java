package ulisboa.tecnico.minesocieties.agents.npc;

import net.kyori.adventure.text.Component;

public final class Message {

    // Private attributes

    private final int durationTicks;
    private final Component[] lines;
    private final int id;

    private static int LAST_ID = 0;

    // Constructors

    public Message(int durationTicks, Component... lines) {
        this.durationTicks = durationTicks;
        this.lines = lines;
        this.id = LAST_ID++;
    }

    // Getters and setters

    public int getDurationTicks() {
        return durationTicks;
    }

    public Component[] getlines() {
        return lines;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Message) obj;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
