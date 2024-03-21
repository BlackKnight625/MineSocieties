package ulisboa.tecnico.minesocieties.agents.location;

import ulisboa.tecnico.minesocieties.MineSocieties;

import java.util.UUID;

public class LocationReference {

    // Private attributes

    private UUID locationUuid;
    private String name;
    private volatile SocialLocation cachedLocation = null;

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
        return name;
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
