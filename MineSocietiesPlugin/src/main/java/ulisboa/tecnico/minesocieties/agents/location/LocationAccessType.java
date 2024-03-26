package ulisboa.tecnico.minesocieties.agents.location;

import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.social.locations.AgentSelectionMenu;
import ulisboa.tecnico.minesocieties.guis.social.locations.PersonalAccessAgentsEditorMenu;
import ulisboa.tecnico.minesocieties.guis.social.locations.SharedAccessAgentsEditorMenu;
import ulisboa.tecnico.minesocieties.utils.StringUtils;

import java.util.List;
import java.util.function.Supplier;

public enum LocationAccessType {
    PUBLIC(PublicAccess.class, PublicAccess::new, Material.OAK_DOOR, "Public Access",
            "All agents can know about this location with no restrictions attached") {
        @Override
        public @Nullable AgentSelectionMenu getAccessAgentsEditor(SocialPlayer player, SocialLocation location, LocationAccess access) {
            return null;
        }
    },
    PERSONAL(PersonalAccess.class, PersonalAccess::new, Material.IRON_DOOR, "Personal Access",
            "Only the specified agent can ever know about this location") {
        @Override
        public AgentSelectionMenu getAccessAgentsEditor(SocialPlayer player, SocialLocation location, LocationAccess access) {
            return new PersonalAccessAgentsEditorMenu(player, location, (PersonalAccess) access);
        }
    },
    SHARED(SharedAccess.class, SharedAccess::new, Material.CRIMSON_DOOR, "Shared Access",
            "Only the agents allowed by this location can ever know about it") {
        @Override
        public AgentSelectionMenu getAccessAgentsEditor(SocialPlayer player, SocialLocation location, LocationAccess access) {
            return new SharedAccessAgentsEditorMenu(player, location, (SharedAccess) access);
        }
    },
    ;

    // Private attributes

    private final Class<? extends LocationAccess> clazz;
    private final Supplier<LocationAccess> supplier;
    private final Material guiMaterial;
    private final String guiName;
    private final List<String> guiDescription;

    // Constructors

    LocationAccessType(Class<? extends LocationAccess> clazz, Supplier<LocationAccess> supplier, Material guiMaterial,
                       String guiName, String guiDescription) {
        this.clazz = clazz;
        this.supplier = supplier;
        this.guiMaterial = guiMaterial;
        this.guiName = guiName;
        this.guiDescription = StringUtils.splitIntoLines(guiDescription, 30);
    }

    // Getters and setters


    public Material getGuiMaterial() {
        return guiMaterial;
    }

    public String getGuiName() {
        return guiName;
    }

    public List<String> getGuiDescription() {
        return guiDescription;
    }

    public Class<? extends LocationAccess> getLocationAccessClass() {
        return clazz;
    }

    public abstract @Nullable AgentSelectionMenu getAccessAgentsEditor(SocialPlayer player, SocialLocation location, LocationAccess access);

    public LocationAccess createInstance() {
        return supplier.get();
    }
}
