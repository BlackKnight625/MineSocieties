package ulisboa.tecnico.minesocieties.llms;

import net.minecraft.server.level.EntityPlayer;
import org.entityutils.entity.npc.player.AnimatedPlayerNPC;
import org.entityutils.utils.data.PlayerNPCData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.actions.socialActions.SendChatTo;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class ChooseActionsFromActionList extends BaseLLMTest {

    // Private attributes

    private SocialAgent agent;

    // Test methods

    @BeforeEach
    public void createAgent() {
        AnimatedPlayerNPC npc = mock(AnimatedPlayerNPC.class);
        PlayerNPCData data = mock(PlayerNPCData.class);
        EntityPlayer entityPlayer = mock(EntityPlayer.class);

        agent = new SocialAgent(npc);

        when(npc.getData()).thenReturn(data);

        when(data.getNpc()).thenReturn(entityPlayer);
        when(data.getName()).thenReturn("Alex Holmes");

        UUID uuid = UUID.randomUUID();

        when(entityPlayer.ct()).thenReturn(uuid);

        MineSocieties.getPlugin().getSocialAgentManager().registerAgent(agent);
    }

    @Test
    public void chooseToChatWithMultiplePeople() {
        SendChatTo sendChatTo = mock(SendChatTo.class);

        when(sendChatTo.getNamesOfNearbyCharacters(agent)).thenReturn(List.of("Steve Johnson", "Jennifer Lopes", "Nathan Daniels"));
    }
}
