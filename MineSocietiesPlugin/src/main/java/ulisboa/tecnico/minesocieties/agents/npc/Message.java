package ulisboa.tecnico.minesocieties.agents.npc;

import net.kyori.adventure.text.Component;

public final class Message {

    // Private attributes

    private final int durationTicks;
    private final Component[] lines;

    // Constructors

    public Message(int durationTicks, Component... lines) {
        this.durationTicks = durationTicks;
        this.lines = lines;
    }

    // Getters and setters

    public int getDurationTicks() {
        return durationTicks;
    }

    public Component[] getlines() {
        return lines;
    }
}
