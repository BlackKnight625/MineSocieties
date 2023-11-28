package ulisboa.tecnico.minesocieties.llms;

import org.entityutils.entity.npc.player.AnimatedPlayerNPC;
import org.entityutils.utils.data.PlayerNPCData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.*;
import ulisboa.tecnico.minesocieties.visitors.CurrentContextExplainer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReflectingOnConversationsTest extends BaseLLMTest {

    // Private attributes

    private SocialAgent agent;

    private String steve = "Steve Johnson";
    private String jennifer = "Jennifer Lopes";
    private String nathan = "Nathan Daniels";
    private AgentReference steveReference = new AgentReference(UUID.randomUUID(), steve);
    private AgentReference jenniferReference = new AgentReference(UUID.randomUUID(), jennifer);
    private AgentReference nathanReference = new AgentReference(UUID.randomUUID(), nathan);
    private AgentReference alexReference;
    private AgentState alexState;

    // Test methods

    @BeforeEach
    public void createAgent() {
        AnimatedPlayerNPC npc = mock(AnimatedPlayerNPC.class);
        PlayerNPCData data = mock(PlayerNPCData.class);

        UUID uuid = new UUID(625, 625);
        String name = "Alex Holmes";

        alexReference = new AgentReference(uuid, name);

        agent = new SocialAgent(npc);

        when(npc.getData()).thenReturn(data);

        when(data.getUUID()).thenReturn(uuid);
        when(data.getName()).thenReturn(name);

        alexState = new AgentState(uuid,
                new AgentPersona(name, 21, Instant.ofEpochSecond(
                        LocalDateTime.of(2000, Month.DECEMBER, 5, 12, 0).toEpochSecond(ZoneOffset.UTC)
                )), new AgentLocation());

        agent.setState(alexState);

        var memory = alexState.getMemory();
        var shortMemory = memory.getShortTermMemory();
        var longMemory = memory.getLongTermMemory();
        var moods = alexState.getMoods();
        var personalities = alexState.getPersonalities();

        shortMemory.addMemorySection(new ShortTermMemorySection(Instant.now(), steve + "'s birthday is in 2 days"));
        shortMemory.addMemorySection(new ShortTermMemorySection(Instant.now(), steve + " invited Alex to his birthday party"));
        longMemory.addMemorySection(new LongTermMemorySection(Instant.now(), "Alex thinks " + steve + " is cute. He likes chocolate"));
        longMemory.addMemorySection(new LongTermMemorySection(Instant.now(), "Alex thinks " + jennifer + " is a liar"));
        longMemory.addMemorySection(new LongTermMemorySection(Instant.now(), jennifer + " stole a pen from Alex"));
        longMemory.addMemorySection(new LongTermMemorySection(Instant.now(), nathan + " helped Alex with a Physics exercise"));
        longMemory.addMemorySection(new LongTermMemorySection(Instant.now().minus(1, ChronoUnit.DAYS),
                jennifer + " slapped " + nathan));
        moods.addState(Mood.HAPPY);
        moods.addState(Mood.LOVE);
        personalities.addState(Personality.INTELLIGENT);
        personalities.addState("nerdy");


        MineSocieties.getPlugin().getSocialAgentManager().registerAgent(agent);
    }

    @Test
    public void reflectOnStolenPenConversation() {
        var conversations = alexState.getMemory().getConversations();

        conversations.addMemorySection(new Conversation(Instant.now(), "Hey Jennifer. Could I please have my pen back?", alexReference, jenniferReference));
        conversations.addMemorySection(new Conversation(Instant.now(), "I lost it. I don't know where it is.", jenniferReference, alexReference));
        conversations.addMemorySection(new Conversation(Instant.now(), "I know you have it. I saw you using it a while ago.", alexReference, jenniferReference));
        conversations.addMemorySection(new Conversation(Instant.now(), "Fine. I'll give it back after lunch.", jenniferReference, alexReference));

        // Reflecting on the conversations
        agent.reflectOnConversationsSync();

        CurrentContextExplainer visitor = new CurrentContextExplainer();

        System.out.println(visitor.explainState(alexState));
    }

    @Test
    public void reflectOnSteveBirthdayPartyConversation() {
        var conversations = alexState.getMemory().getConversations();

        conversations.addMemorySection(new Conversation(Instant.now(), "Hey Steve! Do you know what gifts you want for your party?", alexReference, steveReference));
        conversations.addMemorySection(new Conversation(Instant.now(), "Hey! My gaming mouse broke recently. However, they're quite expensive.", steveReference, alexReference));
        conversations.addMemorySection(new Conversation(Instant.now(), "Ohh, don't worry! Just let me know what model you like and I'll search for a cool one.", alexReference, steveReference));
        conversations.addMemorySection(new Conversation(Instant.now(), "Are you sure? I don't want you to go broke for this.", steveReference, alexReference));
        conversations.addMemorySection(new Conversation(Instant.now(), "Yeah, no problem! It's going to be a big birthday, so a big gift is the least I can do.", alexReference, steveReference));
        conversations.addMemorySection(new Conversation(Instant.now(), "Ohh well, thank you so much! I love LogiTech. Please don't go overboard... There's no need to spend too much.", steveReference, alexReference));

        // Reflecting on the conversations
        agent.reflectOnConversationsSync();

        CurrentContextExplainer visitor = new CurrentContextExplainer();

        System.out.println(visitor.explainState(alexState));
    }
}
