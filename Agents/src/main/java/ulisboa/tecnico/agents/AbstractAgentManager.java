package ulisboa.tecnico.agents;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ulisboa.tecnico.agents.npc.IAgent;
import ulisboa.tecnico.agents.observation.EventListener;
import ulisboa.tecnico.agents.player.IPlayerAgent;
import ulisboa.tecnico.agents.utils.ReadWriteLock;

import java.util.*;
import java.util.function.Consumer;

/**
 *  The Manager of instances of agents. To use this API, all you must do is create an instance of
 * a child of this class, and then call its "initialize" method.
 *  This class allows the creation and management of NPC Agents.
 * @param <A>
 *     A more specialized type of agents
 * @param <P>
 *     A more specialized type of player wrappers
 * @param <C>
 *     The closest superclass of A and P
 */
public abstract class AbstractAgentManager<A extends IAgent, P extends IPlayerAgent, C extends ICharacter> {

    // Private attributes

    private final Map<UUID, ICharacter> characterMap = new HashMap<>();
    private final ReadWriteLock characterMapLock = new ReadWriteLock();
    private final Map<String, ICharacter> characterByNameMap = new HashMap<>();
    private final ReadWriteLock characterByNameMapLock = new ReadWriteLock();
    private final JavaPlugin plugin;

    // Constructors

    public AbstractAgentManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // Getters and setters

    public JavaPlugin getPlugin() {
        return plugin;
    }

    // Other methods

    public void initialize() {
        new EventListener(this).register();
    }

    public A deployAgent(String name, Location location) {
        A agent = getNewAgentInstance(name, location);

        registerAgent(agent);

        return agent;
    }

    /**
     *  Mostly a debug method. Forcibly registers the given agent instance.
     * @param agent
     *  The agent to register
     */
    public void registerAgent(A agent) {
        characterMapLock.write(() -> characterMap.put(agent.getUUID(), agent));
        characterByNameMapLock.write(() -> characterByNameMap.put(agent.getName(), agent));
    }

    public void deleteAgent(UUID uuid) {
        if (characterMap.containsKey(uuid)) {
            ICharacter character = characterMap.get(uuid);

            if (character instanceof IAgent agent) {
                agent.deleted();
                agent.getAgent().setAlive(false);

                characterMapLock.write(() -> characterMap.remove(uuid));
                characterByNameMapLock.write(() -> characterByNameMap.remove(character.getName()));
            } else {
                throw new RuntimeException("Provided UUID (" + uuid + ") does not correspond to an Agent. It " +
                        "corresponds to a Player named " + character.getName());
            }
        }
    }

    public void registerPlayer(Player player) {
        UUID uuid = player.getUniqueId();

        characterMapLock.readLock();

        if (characterMap.containsKey(uuid)) {
            // Player wrapper already exists. Refreshing the player instance inside it
            try {
                P playerWrapper = ((P) characterMap.get(uuid));

                if (!playerWrapper.getName().equals(player.getName())) {
                    // Player changed their name
                    characterByNameMapLock.write(() -> {
                        characterByNameMap.remove(playerWrapper.getName());
                        characterByNameMap.put(player.getName(), playerWrapper);
                    });
                }

                playerWrapper.setPlayer(player);
            } catch (ClassCastException e) {
                throw new RuntimeException("UUID of player (" + uuid + ") is already being used to represent an Agent.");
            } finally {
                characterMapLock.readUnlock();
            }
        } else {
            characterMapLock.readUnlock();

            // Player wrapper does not exist yet
            ICharacter playerWrapper = getNewPlayerWrapper(player);

            characterMapLock.write(() -> characterMap.put(uuid, playerWrapper));
            characterByNameMapLock.write(() -> characterByNameMap.put(player.getName(), playerWrapper));
        }
    }

    public @Nullable A getAgent(UUID uuid) {
        characterMapLock.readLock();
        ICharacter character = characterMap.get(uuid);
        characterMapLock.readUnlock();

        if (character == null) {
            return null;
        } else {
            return (A) character;
        }
    }

    public @NotNull P getPlayerWrapper(UUID uuid) {
        characterMapLock.readLock();
        ICharacter character = characterMap.get(uuid);
        characterMapLock.readUnlock();

        return (P) character;
    }

    public P getPlayerWrapper(Player player) {
        return getPlayerWrapper(player.getUniqueId());
    }

    public C getCharacter(UUID uuid) {
        characterMapLock.readLock();
        ICharacter character = characterMap.get(uuid);
        characterMapLock.readUnlock();

        if (character == null) {
            return null;
        } else {
            return (C) character;
        }
    }

    public @Nullable C getCharacter(String name) {
        characterByNameMapLock.readLock();
        ICharacter character = characterByNameMap.get(name);
        characterByNameMapLock.readUnlock();

        if (character == null) {
            return null;
        } else {
            return (C) character;
        }
    }

    protected abstract A getNewAgentInstance(String name, Location location);

    protected abstract P getNewPlayerWrapper(Player player);

    public void forEachValidCharacter(Consumer<C> apply) {
        characterMapLock.readLock();
        var characters = new ArrayList<>(characterMap.values());
        characterMapLock.readUnlock();

        for (ICharacter character : characters) {
            if (character.isValid()) {
                apply.accept((C) character);
            }
        }
    }

    public void forEachValidAgent(Consumer<A> apply) {
        characterMapLock.readLock();
        var characters = new ArrayList<>(characterMap.values());
        characterMapLock.readUnlock();

        for (ICharacter character : characters) {
            if (character instanceof IAgent && character.isValid()) {
                apply.accept((A) character);
            }
        }
    }

    public void forEachValidPlayer(Consumer<P> apply) {
        characterMapLock.readLock();
        var characters = new ArrayList<>(characterMap.values());
        characterMapLock.readUnlock();

        for (ICharacter character : characters) {
            if (character instanceof IPlayerAgent && character.isValid()) {
                apply.accept((P) character);
            }
        }
    }
}
