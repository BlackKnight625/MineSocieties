package ulisboa.tecnico.minesocieties.visitors;

import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import ulisboa.tecnico.minesocieties.agents.npc.state.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class ContextVisitorTest {

    @Test
    public void visitConversations() {
        AgentConversations alexConversations = new AgentConversations();
        AgentReference alex = new AgentReference(UUID.randomUUID(), "Alex Jones");
        AgentReference steve = new AgentReference(UUID.randomUUID(), "Steve Johnson");
        Instant aWhileAgo = Instant.now().minus(5, ChronoUnit.MINUTES);
        CurrentContextVisitor visitor = new CurrentContextVisitor();

        alexConversations.addMemorySection(new Conversation(aWhileAgo, "Hey Steve! How are you?", alex, steve));
        aWhileAgo = aWhileAgo.plus(15, ChronoUnit.SECONDS);
        alexConversations.addMemorySection(new Conversation(aWhileAgo, "I'm good! Been working on my thesis. What about you?", steve, alex));
        aWhileAgo = aWhileAgo.plus(15, ChronoUnit.SECONDS);
        alexConversations.addMemorySection(new Conversation(aWhileAgo, "I've also been working on my thesis... It's quite a lot!", alex, steve));
        aWhileAgo = aWhileAgo.plus(15, ChronoUnit.SECONDS);
        alexConversations.addMemorySection(new Conversation(aWhileAgo, "Indeed. If you don't mind, I must go back to it. Bye!", steve, alex));

        System.out.println(visitor.explainConversations(alexConversations));
    }

    @Test
    public void visitState() {
        AgentReference alex = new AgentReference(UUID.randomUUID(), "Alex Jones");
        AgentReference steve = new AgentReference(UUID.randomUUID(), "Steve Johnson");
        Instant aWhileAgo = Instant.now().minus(5, ChronoUnit.MINUTES);

        // Creating some conversations
        AgentConversations alexConversations = new AgentConversations();
        alexConversations.addMemorySection(new Conversation(aWhileAgo, "Hey Steve! How are you?", alex, steve));
        aWhileAgo = aWhileAgo.plus(15, ChronoUnit.SECONDS);
        alexConversations.addMemorySection(new Conversation(aWhileAgo, "I'm good! Been working on my thesis. What about you?", steve, alex));
        aWhileAgo = aWhileAgo.plus(15, ChronoUnit.SECONDS);
        alexConversations.addMemorySection(new Conversation(aWhileAgo, "I've also been working on my thesis... It's quite a lot!", alex, steve));
        aWhileAgo = aWhileAgo.plus(15, ChronoUnit.SECONDS);
        alexConversations.addMemorySection(new Conversation(aWhileAgo, "Indeed. If you don't mind, I must go back to it. Bye!", steve, alex));

        // Creating some reflections
        AgentReflections alexReflections = new AgentReflections();
        aWhileAgo.minus(1, ChronoUnit.HOURS);
        alexReflections.addMemorySection(new Reflection(aWhileAgo, "They consider their thesis to be very hard."));
        aWhileAgo.minus(2, ChronoUnit.HOURS);
        alexReflections.addMemorySection(new Reflection(aWhileAgo, "They think Steve Johnson's thesis theme is very interesting."));

        // Creating some opinions

        AgentMemory alexMemory = new AgentMemory(new AgentLocation(new Vector(0, 0, 0), "Earth", "Alex's home"));

        AgentState alexState = new AgentState();
    }
}
