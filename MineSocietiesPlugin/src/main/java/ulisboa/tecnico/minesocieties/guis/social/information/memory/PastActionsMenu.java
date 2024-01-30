package ulisboa.tecnico.minesocieties.guis.social.information.memory;

import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.PastAction;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;

import java.time.Instant;

public class PastActionsMenu extends MemoryChangingMenu<PastAction> {

    // Constructors

    public PastActionsMenu(SocialPlayer player, SocialAgent agent) {
        super(player, agent, agent.getState().getMemory().getPastActions(), "Short term memory");
    }

    // Other methods

    @Override
    protected PastAction newSectionFromLine(String line) {
        return new PastAction(Instant.now(), line);
    }

    @Override
    protected String getExplainedSection(PastAction section) {
        return section.getPastAction();
    }
}
