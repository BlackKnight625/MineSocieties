package ulisboa.tecnico.minesocieties.agents.actions.jobActions;

import ulisboa.tecnico.agents.actions.jobActions.GoFishing;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.actions.ISocialAction;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.visitors.IActionVisitor;

public class InformativeGoFishing extends GoFishing<SocialAgent> implements ISocialAction {

    // Constructors

    public InformativeGoFishing(int amountToFish, int maxFishingTicks, int maxTicksPerFish) {
        super(amountToFish, maxFishingTicks, maxTicksPerFish, MineSocieties.getPlugin().getLogger());
    }

    @Override
    public String accept(IActionVisitor visitor) {
        return visitor.visitGoFishing(this);
    }

    @Override
    public boolean canDoMicroActions() {
        return true;
    }
}