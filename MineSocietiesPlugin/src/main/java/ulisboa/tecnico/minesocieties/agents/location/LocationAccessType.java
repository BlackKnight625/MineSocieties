package ulisboa.tecnico.minesocieties.agents.location;

public enum LocationAccessType {
    PUBLIC(PublicAccess.class),
    PERSONAL(PersonalAccess.class),
    SHARED(SharedAccess.class),
    ;

    // Private attributes

    private final Class<? extends LocationAccess> clazz;

    // Constructors

    LocationAccessType(Class<? extends LocationAccess> clazz) {
        this.clazz = clazz;
    }

    // Getters and setters

    public Class<? extends LocationAccess> getLocationAccessClass() {
        return clazz;
    }
}
