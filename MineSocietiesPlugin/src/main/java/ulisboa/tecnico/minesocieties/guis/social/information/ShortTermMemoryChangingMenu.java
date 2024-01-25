package ulisboa.tecnico.minesocieties.guis.social.information;

import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentLongTermMemory;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentShortTermMemory;
import ulisboa.tecnico.minesocieties.agents.npc.state.LongTermMemorySection;
import ulisboa.tecnico.minesocieties.agents.npc.state.ShortTermMemorySection;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;

import java.time.Instant;

public class ShortTermMemoryChangingMenu extends MemoryChangingMenu<ShortTermMemorySection> {

    // Constructors

    public ShortTermMemoryChangingMenu(SocialPlayer player, SocialAgent agent) {
        super(player, agent, agent.getState().getMemory().getShortTermMemory(), "Short term memory");
    }

    // Other methods

    @Override
    protected ShortTermMemorySection newSectionFromLine(String line) {
        return new ShortTermMemorySection(Instant.now(), line);
    }

    @Override
    protected String getExplainedSection(ShortTermMemorySection section) {
        return section.getMemorySection();
    }
}
