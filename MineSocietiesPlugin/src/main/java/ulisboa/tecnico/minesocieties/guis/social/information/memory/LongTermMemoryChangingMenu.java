package ulisboa.tecnico.minesocieties.guis.social.information.memory;

import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.LongTermMemorySection;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;

import java.time.Instant;

public class LongTermMemoryChangingMenu extends MemoryChangingMenu<LongTermMemorySection> {

    // Constructors

    public LongTermMemoryChangingMenu(SocialPlayer player, SocialAgent agent) {
        super(player, agent, agent.getState().getMemory().getLongTermMemory(), "Long term memory");
    }

    // Other methods

    @Override
    protected LongTermMemorySection newSectionFromLine(String line) {
        return new LongTermMemorySection(Instant.now(), line);
    }

    @Override
    protected String getExplainedSection(LongTermMemorySection section) {
        return section.getMemorySection();
    }
}
