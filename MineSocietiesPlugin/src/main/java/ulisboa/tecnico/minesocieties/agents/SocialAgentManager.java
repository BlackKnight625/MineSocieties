package ulisboa.tecnico.minesocieties.agents;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.K;
import org.entityutils.entity.npc.player.AnimatedPlayerNPC;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.exception.CommandErrorException;
import ulisboa.tecnico.agents.AbstractAgentManager;
import ulisboa.tecnico.agents.ICharacter;
import ulisboa.tecnico.agents.observation.ReceivedChatObservation;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentState;
import ulisboa.tecnico.minesocieties.agents.observation.SocialEventListener;
import ulisboa.tecnico.minesocieties.agents.observation.wrapped.SocialReceivedChatFromObservation;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.utils.ComponentUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;

public class SocialAgentManager extends AbstractAgentManager<SocialAgent, SocialPlayer, SocialCharacter> {

    // Private attributes

    private SocialEventListener eventListener;

    // Public attributes

    public static final Path STATES_PATH =  Path.of("plugins", "MineSocieties", "social_agents");

    public static final Path STATES_BACKUP_PATH_SUFFIX =  Path.of("social_agents");

    // Constructors

    public SocialAgentManager(JavaPlugin plugin) {
        super(plugin);
    }

    // Other methods

    @Override
    public void initialize() {
        eventListener = new SocialEventListener(this);

        eventListener.register();
    }

    @Override
    protected SocialAgent getNewAgentInstance(String name, Location location) {
        return new SocialAgent(new AnimatedPlayerNPC(name, location, getPlugin()));
    }

    @Override
    protected SocialPlayer getNewPlayerWrapper(Player player) {
        return new SocialPlayer(player);
    }

    public SocialAgent deployNewAgent(String name, Location location, @Nullable String description) throws IllegalArgumentException {
        SocialAgent agent = deployAgent(name, location);

        // Turning the initial description into a memory
        if (description != null && !description.isEmpty()) {
            agent.getState().insertDescriptionAsync(description);
        }

        return agent;
    }

    public void talkWith(SocialPlayer player, SocialAgent agent, String message) throws CommandErrorException {
        SocialReceivedChatFromObservation observation = new SocialReceivedChatFromObservation(
                new ReceivedChatObservation(player, message)
        );

        agent.receivedChatFrom(observation);

        if (MineSocieties.getPlugin().isChatBroadcasted()) {
            Bukkit.broadcast(ComponentUtils.sendMessageToPrefix(player.getName(), agent.getName(), message));
        }
    }

    public Path backupsFile(String backupFolderName) {
        return MineSocieties.getPlugin().getBackupsPathPrefix().resolve(backupFolderName).resolve(STATES_BACKUP_PATH_SUFFIX);
    }

    public void loadSavedAgents() throws IOException {
        loadSavedAgents(STATES_PATH);
    }

    public void loadSavedAgents(Path path) throws IOException {
        File agentDirectory = path.toFile();

        if (agentDirectory.exists()) {
            File[] agentFiles = agentDirectory.listFiles();

            for (File agentFile : agentFiles) {
                MineSocieties.getPlugin().getLogger().info("Loading agent from file " + agentFile.getPath());

                try {
                    AgentState loadedState = AgentState.load(agentFile.getAbsolutePath());

                    SocialAgent loadedAgent = deployAgent(
                            loadedState.getPersona().getName(),
                            loadedState.getCurrentLocation().toBukkitLocation(),
                            a -> {
                                // Before deploying the agent, its UUID must be replaced with the loaded one and
                                // the loaded state must be placed in the agent
                                a.getAgent().getData().setUUID(loadedState.getUUID());
                                a.setState(loadedState);
                            }
                    );

                    MineSocieties.getPlugin().getLogger().info("Successfully deployed " + loadedAgent.getName() + " at " +
                            loadedAgent.getLocation());
                } catch (AgentState.StateReadException e) {
                    throw new IOException("Error loading agent stored at " + agentFile.getPath(), e);
                }
            }
        } else {
            MineSocieties.getPlugin().getLogger().info("Creating a directory for storing social agents");

            agentDirectory.mkdirs();
        }
    }

    /**
     *  Called when this manager is empty, to fill it up with agents coming from a backup folder
     * @param backupFolderName
     *  The name of the folder where the agents are stored
     * @throws IOException
     *  If there is an error reading the agents from the backup folder
     */
    public void loadSavedAgentsFromBackup(String backupFolderName) throws IOException {
        File backupFolder = backupsFile(backupFolderName).toFile();

        if (!backupFolder.exists()) {
            throw new CommandErrorException("Backup folder does not exist: " + backupFolder.getAbsolutePath());
        }

        loadSavedAgents(backupFolder.toPath());
    }


    /**
     *  Called when this manager is about to no longer be used to clean up the files associated with its agents.
     */
    public void deleteAllAgents() {
        getCharacterMapLock().writeLock();
        getCharacterByNameMapLock().writeLock();

        try {
            var characters = new ArrayList<>(getCharacterMap().values());

            for (ICharacter character : characters) {
                if (character instanceof SocialAgent agent) {
                    deleteAgentNoLock(agent.getUUID());

                    // Deleting the agent's file
                    agent.getState().getStatePath().toFile().delete();
                }
            }
        } finally {
            getCharacterMapLock().writeUnlock();
            getCharacterByNameMapLock().writeUnlock();
        }
    }

    public void backupAgents(String backupFolderName) throws IOException {
        getCharacterMapLock().readLock();
        getCharacterByNameMapLock().readLock();

        try {
            File backupFolder = backupsFile(backupFolderName).toFile();

            if (!backupFolder.exists()) {
                backupFolder.mkdirs();
            }

            for (ICharacter character : getCharacterMap().values()) {
                if (character instanceof SocialAgent agent) {
                    agent.getState().saveSync(backupFolder.toPath());
                }
            }
        } finally {
            getCharacterMapLock().readUnlock();
            getCharacterByNameMapLock().readUnlock();
        }
    }

    /**
     *  Goes through the agents and deletes locations that no longer exist from their memories
     */
    public void deleteAgentsInvalidLocations() {
        forEachAgent(SocialAgent::deleteAgentsInvalidLocations);
    }

    public void addPlayerAsViewer(Player player) {
        forEachValidAgent(a -> a.getAgent().setAlive2(player, true));
    }

    public void removePlayerAsViewer(Player player) {
        forEachValidAgent(a -> a.getAgent().setAlive2(player, false));
    }

    public void characterDroppedItem(Item item, SocialCharacter character) {
        eventListener.characterDroppedItem(item, character);
    }
}
