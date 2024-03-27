package ulisboa.tecnico.agents;

import org.apache.commons.lang3.text.WordUtils;
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
import java.util.function.Predicate;
import java.util.regex.Pattern;

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

    private static final Pattern NAME_PATTERN = Pattern.compile("[\\w'\\-,.][^0-9_!¡?÷¿/\\\\+=@#$%ˆ&*(){}|~<>;:\\[\\]]{2,}");

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

    public A deployAgent(String name, Location location) throws IllegalArgumentException {
        return deployAgent(name, location, null);
    }

    public A deployAgent(String name, Location location, @Nullable Consumer<A> beforeDeployment) throws IllegalArgumentException {
        // Sanitizing the name
        name = name.replaceAll("\"", "");

        // Making sure capitalization is correct
        name = WordUtils.capitalizeFully(name);

        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("The given name '" + name + "' does not abide to naming conventions. It must " +
                    "follow this regex: " + NAME_PATTERN);
        }

        if (!location.getChunk().isLoaded()) {
            // Loading the chunk before deploying the agent
            location.getChunk().load();
        }

        A agent = getNewAgentInstance(name, location);

        if (beforeDeployment != null) {
            beforeDeployment.accept(agent);
        }

        registerAgent(agent);

        agent.deploy();

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

    public void forEachAgent(Consumer<A> apply) {
        characterMapLock.readLock();
        var characters = new ArrayList<>(characterMap.values());
        characterMapLock.readUnlock();

        for (ICharacter character : characters) {
            if (character instanceof IAgent) {
                apply.accept((A) character);
            }
        }
    }

    /**
     *  Applies the predicate to every valid agent. If the predicate returns true, the next agent gets affected.
     *  If the predicate returns false, no more agents will be affected by the predicate
     * @param apply
     *  The predicate to apply to valid agents
     */
    public void forEachValidAgentUntil(Predicate<A> apply) {
        characterMapLock.readLock();
        var characters = new ArrayList<>(characterMap.values());
        characterMapLock.readUnlock();

        for (ICharacter character : characters) {
            if (character instanceof IAgent && character.isValid()) {
                if (!apply.test((A) character)) {
                    break;
                }
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
