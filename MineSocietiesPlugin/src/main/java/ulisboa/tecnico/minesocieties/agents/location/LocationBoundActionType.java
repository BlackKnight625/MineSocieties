package ulisboa.tecnico.minesocieties.agents.location;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import ulisboa.tecnico.minesocieties.agents.actions.ISocialAction;
import ulisboa.tecnico.minesocieties.agents.actions.jobActions.InformativeFarming;
import ulisboa.tecnico.minesocieties.agents.actions.jobActions.InformativeGoFishing;

import java.util.function.Supplier;

public enum LocationBoundActionType {
    FISHING(() -> new InformativeGoFishing(5, 3 * 60 * 20, 60 * 20), "Fishing", Material.FISHING_ROD),
    FARMING(() -> new InformativeFarming(3 * 60 * 20), "Farming", Material.IRON_HOE),
    ;

    // Private attributes

    private final Supplier<ISocialAction> toSocialAction;
    private final String guiName;
    private final Material guiMaterial;

    // Constructors

    LocationBoundActionType(Supplier<ISocialAction> toSocialAction, String guiName, Material guiMaterial) {
        this.toSocialAction = toSocialAction;
        this.guiName = guiName;
        this.guiMaterial = guiMaterial;
    }

    // Getters and setters

    public String getGuiName() {
        return this.guiName;
    }

    public Material getGuiMaterial() {
        return this.guiMaterial;
    }

    // Other methods

    public ISocialAction toNewSocialAction() {
        return toSocialAction.get();
    }

    /**
     *  Checks if an action of this kind could possibly be executed by anyone at the given Location.
     * This is used to check if an action of this kind can be associated with a SocialLocation.
     *  For example, a SocialLocation should not be able to have a InformativeGoFishing action associated with it if
     * there's no water nearby.
     * @param location
     *  The given location
     * @return
     *  A Pair holding true if an action of this kind could possibly be executed by anyone at the given Location or
     * holding false and a String with the reason why it can't be executed.
     */
    public Pair<Boolean, String> canBeExecutedInLocation(Location location) {
        return toNewSocialAction().canBeExecutedInLocation(location);
    }
}
