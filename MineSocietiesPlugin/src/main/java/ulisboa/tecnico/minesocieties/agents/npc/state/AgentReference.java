package ulisboa.tecnico.minesocieties.agents.npc.state;

import ulisboa.tecnico.agents.ICharacter;
import ulisboa.tecnico.agents.npc.IAgent;

import java.util.UUID;

public class AgentReference {

    // Private attributes

    private final UUID uuid;
    private final String name;

    // Constructors

    public AgentReference(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public AgentReference(ICharacter agent) {
        this.uuid = agent.getUUID();
        this.name = agent.getName();
    }

    // Getters and setters

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    // Other methods

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentReference that = (AgentReference) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
