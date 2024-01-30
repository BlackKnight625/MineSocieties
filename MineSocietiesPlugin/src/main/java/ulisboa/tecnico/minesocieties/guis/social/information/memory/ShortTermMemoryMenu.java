package ulisboa.tecnico.minesocieties.guis.social.information.memory;

import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.ShortTermMemorySection;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;

import java.time.Instant;

public class ShortTermMemoryMenu extends MemoryChangingMenu<ShortTermMemorySection> {

    // Constructors

    public ShortTermMemoryMenu(SocialPlayer player, SocialAgent agent) {
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
