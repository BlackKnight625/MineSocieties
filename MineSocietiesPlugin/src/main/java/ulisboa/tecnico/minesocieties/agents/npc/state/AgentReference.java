package ulisboa.tecnico.minesocieties.agents.npc.state;

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

    public AgentReference(IAgent agent) {
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
}
