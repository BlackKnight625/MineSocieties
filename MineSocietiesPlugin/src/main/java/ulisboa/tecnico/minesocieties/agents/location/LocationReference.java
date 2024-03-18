package ulisboa.tecnico.minesocieties.agents.location;

import ulisboa.tecnico.minesocieties.MineSocieties;

import java.util.UUID;

public class LocationReference {

    // Private attributes

    private UUID locationUuid;
    private volatile SocialLocation cachedLocation = null;

    // Constructors

    public LocationReference(SocialLocation socialLocation) {
        this.locationUuid = socialLocation.getUuid();
        this.cachedLocation = socialLocation;
    }

    public LocationReference() {
        // Constructor for GSON
    }

    // Getters and setters

    public UUID getLocationUuid() {
        return locationUuid;
    }

    public SocialLocation getLocation() {
        if (cachedLocation == null) {
            cachedLocation = MineSocieties.getPlugin().getLocationsManager().getLocation(locationUuid);
        }

        return cachedLocation;
    }
}
