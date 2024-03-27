package ulisboa.tecnico.minesocieties.agents.location;

import ulisboa.tecnico.minesocieties.MineSocieties;

import java.util.UUID;

public class LocationReference {

    // Private attributes

    private UUID locationUuid;
    /**
     *  This exists so that GSON stores the name of the location, for players to be able to read them if they decide
     * to look into the agent's json files. This name might be outdated if a player has changed it in-game. However, it
     * will eventually fix itself since getLocation() replaces the name with the current one when cachedLocation is null.
     */
    private String name;
    private transient SocialLocation cachedLocation = null;

    // Constructors

    public LocationReference(SocialLocation socialLocation) {
        this.locationUuid = socialLocation.getUuid();
        this.cachedLocation = socialLocation;
        this.name = socialLocation.getName();
    }

    public LocationReference() {
        // Constructor for GSON
    }

    // Getters and setters

    public UUID getLocationUuid() {
        return locationUuid;
    }

    public String getName() {
        SocialLocation location = getLocation();

        if (location == null) {
            return name;
        } else {
            return location.getName();
        }
    }

    public SocialLocation getLocation() {
        if (cachedLocation == null) {
            cachedLocation = MineSocieties.getPlugin().getLocationsManager().getLocation(locationUuid);

            // Name might have been changed
            name = cachedLocation.getName();
        }

        if (cachedLocation.isDeleted()) {
            return null;
        }

        return cachedLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationReference reference = (LocationReference) o;

        return locationUuid.equals(reference.locationUuid);
    }

    @Override
    public int hashCode() {
        return locationUuid.hashCode();
    }
}
