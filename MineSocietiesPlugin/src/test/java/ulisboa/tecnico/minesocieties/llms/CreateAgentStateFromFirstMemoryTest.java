package ulisboa.tecnico.minesocieties.llms;

import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentLocation;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentPersona;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentState;
import ulisboa.tecnico.minesocieties.visitors.CurrentContextVisitor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;

public class CreateAgentStateFromFirstMemoryTest extends BaseLLMTest {

    private AgentState getNewAgentState() {
        return new AgentState(
                new AgentPersona("Alex Johnson", 23, Instant.ofEpochSecond(
                        LocalDateTime.of(2000, Month.DECEMBER, 5, 12, 0).toEpochSecond(ZoneOffset.UTC)
                )),
                new AgentLocation(new Vector(0, 0, 0), "Earth", "Alex's home")
        );
    }

    @Test
    public void fromSmallFirstMemory() {
        AgentState agentState = getNewAgentState();

        agentState.insertDescriptionSync("""
                Alex has always liked programming.
                Her favourite language is Java.
                She does not like C++ as it's too complicated.
                She thinks Steve is very smart, adorable, and hard-working.
                She's been through a lot lately.
                """);

        CurrentContextVisitor visitor = new CurrentContextVisitor();

        System.out.println(visitor.explainState(agentState));
    }
}
