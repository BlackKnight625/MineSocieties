package ulisboa.tecnico.minesocieties.agents.location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ulisboa.tecnico.agents.utils.ReadWriteLock;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.CharacterReference;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class LocationsManager {

    // Private attributes

    private final Map<UUID, SocialLocation> locations = new HashMap<>();
    private final ReadWriteLock locationsLock = new ReadWriteLock();
    private final Gson gson;

    // Public attributes

    public static final Path LOCATIONS_PATH =  Path.of("plugins", "MineSocieties", "locations");

    // Constructors

    public LocationsManager() {
        GsonBuilder builder = new GsonBuilder();

        builder.setPrettyPrinting();
        builder.registerTypeAdapter(LocationAccess.class, new LocationAccessJsonDeserializer());

        gson = builder.create();
    }

    // Other methods

    public void addLocation(SocialLocation location) {
        locationsLock.write(() -> locations.put(location.getUuid(), location));
    }

    public SocialLocation getLocation(UUID uuid) {
        locationsLock.readLock();
        SocialLocation socialLocation = locations.get(uuid);
        locationsLock.readUnlock();

        return socialLocation;
    }

    public String toFileName(SocialLocation location) {
        return location.getName() + "_" + location.getUuid() + ".json";
    }

    public void saveAsync() {
        MineSocieties.getPlugin().getThreadPool().execute(this::saveSync);
    }

    public void saveAsync(SocialLocation location) {
        MineSocieties.getPlugin().getThreadPool().execute(() -> saveSync(location));
    }

    public void saveSync() {
        locationsLock.readLock();
        var locationsCopy = new ArrayList<>(locations.values());
        locationsLock.readUnlock();

        // Writing the locations one by one
        for (SocialLocation location : locationsCopy) {
            saveSync(location);
        }
    }

    public void saveSync(SocialLocation location) {
        try {
            Files.writeString(LOCATIONS_PATH.resolve(toFileName(location)), gson.toJson(location));
        } catch (IOException e) {
            MineSocieties.getPlugin().getLogger().severe("Unable to save location '" + location.getName() + "' to a file.");
            e.printStackTrace();
        }
    }

    public void loadSync() throws IOException {
        File locationsDirecotry = LOCATIONS_PATH.toFile();

        if (!locationsDirecotry.exists()) {
            locationsDirecotry.mkdirs();
        }

        for (File file : locationsDirecotry.listFiles()) {
            if (file.isFile()) {
                SocialLocation location = gson.fromJson(Files.readString(file.toPath()), SocialLocation.class);

                addLocation(location);

                // Checking if the location's name is the same as the file's name
                if (!toFileName(location).equals(file.getName())) {
                    // Someone editted the location's name inside the Json file. Changing the file's name accordingly
                    file.renameTo(LOCATIONS_PATH.resolve(toFileName(location)).toFile());
                }
            }
        }
    }

    public void checkAndDeleteInvalidLocationsSync() {
        locationsLock.write(() -> {
            Iterator<SocialLocation> iterator = locations.values().iterator();

            while (iterator.hasNext()) {
                SocialLocation location = iterator.next();

                location.fixInconsistencies();

                if (!location.isValid()) {
                    iterator.remove();

                    try {
                        Files.delete(LOCATIONS_PATH.resolve(toFileName(location)));
                    } catch (IOException e) {
                        MineSocieties.getPlugin().getLogger().severe("Unable to delete invalid location \"" +
                                location.getName() + "\"'s file");
                        e.printStackTrace();
                    }
                }
            }
        });

        saveSync();
    }

    public void deleteAsync(SocialLocation location) {
        var agenstAffected = location.getStronglyConnectedAgentsCopy();

        // Deleting the file in another thread
        MineSocieties.getPlugin().getThreadPool().execute(() -> {
            AtomicBoolean deleted = new AtomicBoolean(false);

            locationsLock.write(() -> {
                locations.remove(location.getUuid());

                try {
                    Files.delete(LOCATIONS_PATH.resolve(toFileName(location)));
                } catch (IOException e) {
                    MineSocieties.getPlugin().getLogger().severe("Unable to delete location \"" + location.getName() + "\"'s file");
                    e.printStackTrace();
                }

                deleted.set(true);
            });

            if (deleted.get()) {
                // Successfully deleted the location. Must update the agents whose memories included this location
                for (CharacterReference reference : agenstAffected) {
                    SocialAgent agent = (SocialAgent) reference.getReferencedCharacter();

                    if (agent != null) {
                        agent.getState().getMemory().getKnownLocations().remove(location.toReference());
                        agent.getState().markDirty();
                    }
                }
            }
        });
    }
}
