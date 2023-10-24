package ulisboa.tecnico.minesocieties.agents.npc.state;

import java.util.UUID;

public class Opinion {

    // Private attributes

    private AgentReference agentReference;
    private String opinion;

    // Constructors

    public Opinion() {}

    public Opinion(AgentReference agentReference, String opinion) {
        this.agentReference = agentReference;
        this.opinion = opinion;
    }

    // Getters and setters

    public UUID getOthersUuid() {
        return agentReference.getUuid();
    }

    public String getOthersName() {
        return agentReference.getName();
    }

    public String getOpinion() {
        return opinion;
    }
}
