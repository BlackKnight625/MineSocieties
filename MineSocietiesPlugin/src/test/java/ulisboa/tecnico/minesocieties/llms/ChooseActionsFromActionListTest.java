package ulisboa.tecnico.minesocieties.llms;

import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.entityutils.entity.npc.player.AnimatedPlayerNPC;
import org.entityutils.utils.data.PlayerNPCData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulisboa.tecnico.agents.observation.WeatherChangeObservation;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.actions.ISocialAction;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.Idle;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.InformativeGoTo;
import ulisboa.tecnico.minesocieties.agents.actions.otherActions.WaitFor;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.SendChatTo;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChooseActionsFromActionListTest extends BaseLLMTest {

    // Private attributes

    private SocialAgent agent;

    private String steve = "Steve Johnson";
    private String jennifer = "Jennifer Lopes";
    private String nathan = "Nathan Daniels";

    // Test methods

    @BeforeEach
    public void createAgent() {
        AnimatedPlayerNPC npc = mock(AnimatedPlayerNPC.class);
        PlayerNPCData data = mock(PlayerNPCData.class);

        UUID uuid = new UUID(625, 625);
        String name = "Alex Holmes";

        agent = new SocialAgent(npc);

        when(npc.getData()).thenReturn(data);

        when(data.getUUID()).thenReturn(uuid);
        when(data.getName()).thenReturn(name);

        AgentState state = new AgentState(uuid,
                new AgentPersona(name, 21, Instant.ofEpochSecond(
                        LocalDateTime.of(2000, Month.DECEMBER, 5, 12, 0).toEpochSecond(ZoneOffset.UTC)
                )), new AgentLocation());

        agent.setState(state);

        var memory = state.getMemory();
        var shortMemory = memory.getShortTermMemory();
        var opinions = memory.getOpinions();
        var reflections = memory.getReflections();
        var notionOfEvents = memory.getNotionOfEvents();
        var moods = state.getMoods();
        var personalities = state.getPersonalities();

        shortMemory.addMemorySection(new ShortTermMemorySection(Instant.now(), steve + "'s birthday is in 2 days"));
        shortMemory.addMemorySection(new ShortTermMemorySection(Instant.now(), steve + " invited Alex to his birthday party"));
        opinions.formOpinion(steve, new Opinion("Alex thinks " + steve + " is cute. He likes chocolate"));
        opinions.formOpinion(jennifer, new Opinion("Alex thinks " + jennifer + " is a liar"));
        reflections.addMemorySection(new Reflection(Instant.now(), jennifer + " stole a pen from Alex"));
        reflections.addMemorySection(new Reflection(Instant.now(), nathan + " helped Alex with a Physics exercise"));
        notionOfEvents.addMemorySection(new NotionOfEvent(Instant.now().minus(1, ChronoUnit.DAYS),
                jennifer + " slapped " + nathan));
        moods.addState(Mood.HAPPY);
        moods.addState(Mood.LOVE);
        personalities.addState(Personality.INTELLIGENT);
        personalities.addState("nerdy");

        MineSocieties.getPlugin().getSocialAgentManager().registerAgent(agent);
    }

    @Test
    public void chooseToChatWithMultiplePeople() {
        SendChatTo sendChatTo = mock(SendChatTo.class); // It's a mock due to its method that searches for nearby entities

        when(sendChatTo.getNamesOfNearbyCharacters(agent)).thenReturn(List.of(steve, jennifer, nathan));
        when(sendChatTo.acceptArgumentsExplainer(any(), any())).thenCallRealMethod();
        when(sendChatTo.accept(any())).thenCallRealMethod();

        List<ISocialAction> possibleActions = new ArrayList<>();

        possibleActions.add(sendChatTo);

        var messages = agent.getPromptForNewAction(possibleActions, new WeatherChangeObservation(WeatherType.DOWNFALL));

        String reply = MineSocieties.getPlugin().getLLMManager().promptSync(messages);

        System.out.println(reply);
    }

    @Test
    public void chooseFromAVarietyOfActions() {
        SendChatTo sendChatTo = mock(SendChatTo.class);

        when(sendChatTo.getNamesOfNearbyCharacters(agent)).thenReturn(List.of(jennifer, nathan));
        when(sendChatTo.acceptArgumentsExplainer(any(), any())).thenCallRealMethod();
        when(sendChatTo.accept(any())).thenCallRealMethod();

        List<ISocialAction> possibleActions = new ArrayList<>();

        possibleActions.add(sendChatTo);
        possibleActions.add(new WaitFor("rain to stop falling"));
        possibleActions.add(new InformativeGoTo(new Location(null, 0, 0, 0), "home"));
        possibleActions.add(new Idle());

        var messages = agent.getPromptForNewAction(possibleActions, new WeatherChangeObservation(WeatherType.DOWNFALL));

        String reply = MineSocieties.getPlugin().getLLMManager().promptSync(messages);

        System.out.println(reply);
    }
}
