package ulisboa.tecnico.minesocieties.llms;

import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentLocation;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentPersona;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentState;
import ulisboa.tecnico.minesocieties.visitors.CurrentContextExplainer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.UUID;

public class CreateAgentStateFromDescriptionTest extends BaseLLMTest {

    private AgentState getNewAgentState() {
        return new AgentState(new UUID(625, 625),
                new AgentPersona("Alex Johnson", Instant.ofEpochSecond(
                        LocalDateTime.of(2000, Month.DECEMBER, 5, 12, 0).toEpochSecond(ZoneOffset.UTC)
                )),
                new AgentLocation(new Vector(0, 0, 0), "Earth", "Alex's home")
        );
    }

    @Test
    public void fromSmallDescription() {
        AgentState agentState = getNewAgentState();

        agentState.insertDescriptionSync("""
                Alex has always liked programming.
                Her favourite language is Java.
                She does not like C++ as it's too complicated.
                She thinks Steve is very smart, adorable, and hard-working.
                She's been through a lot lately.
                Her cat is currently hungry.
                """);

        CurrentContextExplainer visitor = new CurrentContextExplainer();

        System.out.println(visitor.explainState(agentState));
    }

    @Test
    public void fromBigDescription() {
        AgentState agentState = getNewAgentState();

        agentState.insertDescriptionSync("""
                Alex has always liked programming.
                She made a program in Java about a snake eating fruit and growing as large as possible.
                She had a lot of fun doing that project.
                Her favourite language is Java.
                She does not like C++ as it's too complicated.
                She had to create a file storage application in C++ and ran into a lot of problems, mostly Segmentation Faults.
                She thinks Steve is very smart, adorable, and hard-working. She's done a few projects with him, and found his methods very resourceful.
                She's been through a lot lately. Her cat is sick, and the veterinarians don't know what's wrong with it.
                Last night, Steve invited her to his friend's birthday party. She really wants to go.
                """);

        CurrentContextExplainer visitor = new CurrentContextExplainer();

        System.out.println(visitor.explainState(agentState));
    }
}
