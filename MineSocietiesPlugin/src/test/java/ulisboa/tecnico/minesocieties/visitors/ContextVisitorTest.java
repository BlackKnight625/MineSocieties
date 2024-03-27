package ulisboa.tecnico.minesocieties.visitors;

import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import ulisboa.tecnico.minesocieties.agents.location.LocationReference;
import ulisboa.tecnico.minesocieties.agents.location.PublicAccess;
import ulisboa.tecnico.minesocieties.agents.location.SocialLocation;
import ulisboa.tecnico.minesocieties.agents.npc.state.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class ContextVisitorTest {

    @Test
    public void visitConversations() {
        AgentConversations alexConversations = new AgentConversations();
        CharacterReference alex = new CharacterReference(UUID.randomUUID(), "Alex Jones");
        CharacterReference steve = new CharacterReference(UUID.randomUUID(), "Steve Johnson");
        Instant aWhileAgo = Instant.now().minus(5, ChronoUnit.MINUTES);

        alexConversations.addMemorySection(new Conversation(aWhileAgo, "Hey Steve! How are you?", alex, steve));
        aWhileAgo = aWhileAgo.plus(15, ChronoUnit.SECONDS);
        alexConversations.addMemorySection(new Conversation(aWhileAgo, "I'm good! Been working on my thesis. What about you?", steve, alex));
        aWhileAgo = aWhileAgo.plus(15, ChronoUnit.SECONDS);
        alexConversations.addMemorySection(new Conversation(aWhileAgo, "I've also been working on my thesis... It's quite a lot!", alex, steve));
        aWhileAgo = aWhileAgo.plus(15, ChronoUnit.SECONDS);
        alexConversations.addMemorySection(new Conversation(aWhileAgo, "Indeed. If you don't mind, I must go back to it. Bye!", steve, alex));

        CurrentContextExplainer visitor = new CurrentContextExplainer();

        System.out.println(visitor.explainConversations(alexConversations));
    }

    @Test
    public void visitState() {
        CharacterReference alex = new CharacterReference(UUID.randomUUID(), "Alex Jones");
        CharacterReference steve = new CharacterReference(UUID.randomUUID(), "Steve Johnson");
        Instant aWhileAgo = Instant.now().minus(5, ChronoUnit.MINUTES);
        AgentState alexState = new AgentState(new UUID(625, 625),
                new AgentPersona(alex.getName(), Instant.ofEpochSecond(
                        LocalDateTime.of(2000, Month.DECEMBER, 5, 12, 0).toEpochSecond(ZoneOffset.UTC)
                ))
        );
        AgentMemory alexMemory = alexState.getMemory();

        alexMemory.setHome(new LocationReference(new SocialLocation(new Vector(0, 0, 0), "Earth", "Alex's home", new PublicAccess())));

        // Creating some conversations
        AgentConversations alexConversations = alexMemory.getConversations();
        alexConversations.addMemorySection(new Conversation(aWhileAgo, "Hey Steve! How are you?", alex, steve));
        aWhileAgo = aWhileAgo.plus(15, ChronoUnit.SECONDS);
        alexConversations.addMemorySection(new Conversation(aWhileAgo, "I'm good! Been working on my thesis. What about you?", steve, alex));
        aWhileAgo = aWhileAgo.plus(15, ChronoUnit.SECONDS);
        alexConversations.addMemorySection(new Conversation(aWhileAgo, "I've also been working on my thesis... It's quite a lot!", alex, steve));
        aWhileAgo = aWhileAgo.plus(15, ChronoUnit.SECONDS);
        alexConversations.addMemorySection(new Conversation(aWhileAgo, "Indeed. If you don't mind, I must go back to it. Bye!", steve, alex));

        // Creating some long term memories
        AgentLongTermMemory longTermMemory = alexMemory.getLongTermMemory();
        aWhileAgo = aWhileAgo.minus(1, ChronoUnit.HOURS);
        longTermMemory.addMemorySection(new LongTermMemorySection(aWhileAgo, "They consider their thesis to be very hard"));
        aWhileAgo = aWhileAgo.minus(2, ChronoUnit.HOURS);
        longTermMemory.addMemorySection(new LongTermMemorySection(aWhileAgo, "They think Steve Johnson's thesis theme is very interesting"));

        // Creating moods
        AgentMoods alexMoods = alexState.getMoods();
        alexMoods.addState(Mood.DEPRESSED, Mood.ANXIOUS);

        // Creating personalities
        AgentPersonalities alexPersonalities = alexState.getPersonalities();
        alexPersonalities.addState(Personality.ADVENTUROUS, Personality.LIBERAL);

        // Testing the visitor
        CurrentContextExplainer visitor = new CurrentContextExplainer();

        System.out.println(visitor.explainState(alexState));
    }

    @Test
    public void visitEmptyState() {
        CharacterReference alex = new CharacterReference(UUID.randomUUID(), "Alex Jones");

        AgentState alexState = new AgentState(new UUID(625, 625),
                new AgentPersona(alex.getName(), Instant.ofEpochSecond(
                        LocalDateTime.of(2000, Month.DECEMBER, 5, 12, 0).toEpochSecond(ZoneOffset.UTC)
                ))
        );

        alexState.getMemory().setHome(new LocationReference(new SocialLocation(new Vector(0, 0, 0), "Earth", "Alex's home", new PublicAccess())));

        // Testing the visitor
        CurrentContextExplainer visitor = new CurrentContextExplainer();

        System.out.println(visitor.explainState(alexState));
    }
}
