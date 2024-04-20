package ulisboa.tecnico.minesocieties.agents;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.entityutils.entity.npc.player.AnimatedPlayerNPC;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.exception.CommandErrorException;
import ulisboa.tecnico.agents.AbstractAgentManager;
import ulisboa.tecnico.agents.observation.ReceivedChatObservation;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.location.SocialLocation;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.npc.state.AgentState;
import ulisboa.tecnico.minesocieties.agents.observation.SocialEventListener;
import ulisboa.tecnico.minesocieties.agents.observation.wrapped.SocialReceivedChatFromObservation;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.utils.ComponentUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class SocialAgentManager extends AbstractAgentManager<SocialAgent, SocialPlayer, SocialCharacter> {

    // Private attributes

    private SocialEventListener eventListener;

    // Public attributes

    public static final Path STATES_PATH =  Path.of("plugins", "MineSocieties", "social_agents");

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

    public void loadSavedAgents() throws IOException {
        File agentDirectory = new File(STATES_PATH.toUri());

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
                    MineSocieties.getPlugin().getLogger().severe("Error loading a saved agent state.");

                    throw new IOException(e);
                }
            }
        } else {
            MineSocieties.getPlugin().getLogger().info("Creating a directory for storing social agents");

            agentDirectory.mkdirs();
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
