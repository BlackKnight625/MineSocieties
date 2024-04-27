package ulisboa.tecnico.minesocieties.agents.actions.jobActions;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import ulisboa.tecnico.agents.actions.jobActions.Farming;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.actions.ISocialAction;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.visitors.IActionVisitor;

public class InformativeFarming extends Farming<SocialAgent> implements ISocialAction {

    // Constructors

    public InformativeFarming(int maxFarmingTicks) {
        super(maxFarmingTicks, MineSocieties.getPlugin().getMaxFarmingRadius(), MineSocieties.getPlugin());
    }

    // Other methods

    @Override
    public String accept(IActionVisitor visitor) {
        return visitor.visitFarming(this);
    }

    @Override
    public boolean canDoMicroActions() {
        return true;
    }

    @Override
    public Pair<Boolean, String> canBeExecutedInLocation(Location location) {
        if (getNearbyFarmlandBlocks(location).isEmpty()) {
            return Pair.of(false, "There are no nearby farmland blocks to farm.");
        } else {
            return Pair.of(true, "");
        }
    }
}
