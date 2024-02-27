package ulisboa.tecnico.minesocieties.agents.npc.state;

import org.jetbrains.annotations.Nullable;
import ulisboa.tecnico.agents.ICharacter;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.SocialCharacter;

import java.util.UUID;

public class CharacterReference {

    // Private attributes

    private final UUID uuid;
    private final String name;

    // Constructors

    public CharacterReference(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public CharacterReference(ICharacter agent) {
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

    public @Nullable SocialCharacter getReferencedCharacter() {
        return MineSocieties.getPlugin().getSocialAgentManager().getCharacter(uuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharacterReference that = (CharacterReference) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "CharacterReference{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                '}';
    }
}
