package ulisboa.tecnico.minesocieties.agents.location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import revxrsal.commands.exception.CommandErrorException;
import ulisboa.tecnico.agents.utils.ReadWriteLock;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentLocation;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentMemory;
import ulisboa.tecnico.minesocieties.agents.npc.state.CharacterReference;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;

/**
 *  Manages the SocialLocation instances. SocialLocations are added and deleted very rarely, so it's fine
 * to use access methods in the Main thread.
 */
public class LocationsManager {

    // Private attributes

    private final Map<UUID, SocialLocation> locations = new HashMap<>();
    private final ReadWriteLock locationsLock = new ReadWriteLock();
    private final Gson gson;

    // Public attributes

    public static final Path LOCATIONS_PATH =  Path.of("plugins", "MineSocieties", "locations");
    public static final Path LOCATIONS_BACKUP_PATH_SUFFIX =  Path.of("locations");

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
        return location.getUuid() + ".json";
    }

    public void saveAsync() {
        saveAsync(LOCATIONS_PATH);
    }

    public void saveAsync(Path path) {
        MineSocieties.getPlugin().getThreadPool().execute(() -> {
            try {
                saveSync(path);
            } catch (IOException e) {
                MineSocieties.getPlugin().getLogger().log(Level.SEVERE, "Unable to save locations at " + path, e);
            }
        });
    }

    public void saveAsync(SocialLocation location) {
        saveAsync(location, LOCATIONS_PATH);
    }

    public void saveAsync(SocialLocation location, Path path) {
        MineSocieties.getPlugin().getThreadPool().execute(() -> {
            try {
                saveSync(location, path);
            } catch (IOException e) {
                MineSocieties.getPlugin().getLogger().log(Level.SEVERE, "Unable to save location \"" +
                        location.getName() + "\" at " + path, e);
            }
        });
    }

    public void saveSync() throws IOException {
        saveSync(LOCATIONS_PATH);
    }

    public void saveSync(Path path) throws IOException {
        locationsLock.readLock();
        var locationsCopy = new ArrayList<>(locations.values());
        locationsLock.readUnlock();

        // Writing the locations one by one
        for (SocialLocation location : locationsCopy) {
            saveSync(location, path);
        }
    }

    public void saveSync(LocationReference reference, Path path) throws IOException {
        SocialLocation location = reference.getLocation();

        if (location != null) {
            saveSync(location, path);
        }
    }

    public void saveSync(SocialLocation location) throws IOException {
        saveSync(location, LOCATIONS_PATH);
    }

    public void saveSync(SocialLocation location, Path path) throws IOException {
        if (!location.isDeleted()) {
            try {
                Files.writeString(path.resolve(toFileName(location)), gson.toJson(location));
            } catch (IOException e) {
                throw new IOException("Unable to save location '" + location.getName() + "' to a file.", e);
            }
        }

        // Else: Location was deleted. It should not be saved.
    }

    public Path backupsPath(String backupFolderName) {
        return MineSocieties.getPlugin().getBackupsPathPrefix().resolve(backupFolderName).resolve(LOCATIONS_BACKUP_PATH_SUFFIX);
    }

    public void saveBackupSync(String backupFolderName) throws IOException {
        locationsLock.readLock();

        File backupFolder = backupsPath(backupFolderName).toFile();

        // Creating the backup folder if it doesn't exist
        if (!backupFolder.exists()) {
            backupFolder.mkdirs();
        }

        // Saving all locations to the backup folder
        for (SocialLocation location : locations.values()) {
            saveSync(location, backupFolder.toPath());
        }

        locationsLock.readUnlock();
    }

    public void loadSync() throws IOException {
        loadSync(LOCATIONS_PATH);
    }

    public void loadSync(Path path) throws IOException {
        File locationsDirectory = path.toFile();

        if (!locationsDirectory.exists()) {
            locationsDirectory.mkdirs();
        }

        for (File file : locationsDirectory.listFiles()) {
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

    /**
     *  Called when this manager is empty, to fill it up with locations coming from a backup folder
     * @param backupFolderName
     *  The name of the folder where the locations are stored
     * @throws IOException
     *  If there is an error reading the locations from the backup folder
     */
    public void loadBackupSync(String backupFolderName) throws IOException {
        File backupFolder = backupsPath(backupFolderName).toFile();

        if (!backupFolder.exists()) {
            throw new CommandErrorException("Backup folder does not exist: " + backupFolder.getAbsolutePath());
        }

        loadSync(backupFolder.toPath());
    }

    public void checkAndDeleteInvalidLocationsSync() {
        locationsLock.write(() -> {
            Iterator<SocialLocation> iterator = locations.values().iterator();

            while (iterator.hasNext()) {
                SocialLocation location = iterator.next();

                boolean fixedInconsistencies = location.fixInconsistencies();
                boolean validAccess = location.isAccessValid();

                if (validAccess && fixedInconsistencies) {
                    // Saving this location since it was fixed
                    try {
                        saveSync();
                    } catch (IOException e) {
                        MineSocieties.getPlugin().getLogger().log(Level.SEVERE, "Unable to save location \"" +
                                location.getName() + "\"", e);
                    }
                }

                if (!validAccess) {
                    iterator.remove();

                    try {
                        Files.delete(LOCATIONS_PATH.resolve(toFileName(location)));
                    } catch (IOException e) {
                        MineSocieties.getPlugin().getLogger().log(Level.SEVERE, "Unable to delete invalid location \"" +
                                location.getName() + "\"'s file", e);
                    }
                }
            }
        });
    }

    public void deleteAsync(LocationReference reference) {
        SocialLocation location = reference.getLocation();

        if (location != null) {
            deleteAsync(location);
        }
    }

    public void deleteAsync(SocialLocation location) {
        var agenstAffected = location.getAgentsWithAccessCopy();

        if (location.isDeleted()) {
            return; // No work to do. Location has already been dealt with
        }

        // Deleting the file in another thread
        MineSocieties.getPlugin().getThreadPool().execute(() -> {
            boolean deleted = false;

            try {
                Files.delete(LOCATIONS_PATH.resolve(toFileName(location)));

                deleted = true;
            } catch (NoSuchFileException e) {
                // File doesn't exist. Nothing to worry about
            } catch (IOException e) {
                MineSocieties.getPlugin().getLogger().log(Level.SEVERE, "Unable to delete location \"" + location.getName() + "\"'s file", e);
            }

            if (deleted) {
                locationsLock.write(() -> {
                    locations.remove(location.getUuid());
                    location.deleted();
                });

                // Successfully deleted the location. Must update the agents whose memories included this location
                for (CharacterReference reference : agenstAffected) {
                    SocialAgent agent = (SocialAgent) reference.getReferencedCharacter();

                    if (agent != null) {
                        AgentMemory memory = agent.getState().getMemory();

                        memory.getKnownLocations().remove(location.toReference());
                        agent.getState().markDirty();
                    }
                }
            }
        });
    }

    /**
     *  Called when this manager is about to no longer be used to clean up the files associated with its locations.
     */
    public void deleteAllLocations() {
        locationsLock.write(() -> {
            for (SocialLocation location : locations.values()) {
                LOCATIONS_PATH.resolve(toFileName(location)).toFile().delete();
                location.deleted();
            }

            locations.clear();
        });
    }

    public List<SocialLocation> getAllLocations() {
        locationsLock.readLock();
        List<SocialLocation> locationsCopy = new ArrayList<>(locations.values());
        locationsLock.readUnlock();

        return locationsCopy;
    }
}
