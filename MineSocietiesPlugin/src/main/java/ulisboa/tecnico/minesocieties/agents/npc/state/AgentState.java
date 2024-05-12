package ulisboa.tecnico.minesocieties.agents.npc.state;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.bukkit.inventory.Inventory;
import org.entityutils.entity.npc.player.AnimatedPlayerNPC;
import ulisboa.tecnico.llms.LLMMessage;
import ulisboa.tecnico.llms.LLMRole;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.SocialAgentManager;
import ulisboa.tecnico.minesocieties.agents.actions.exceptions.MalformedNewStateResponseException;
import ulisboa.tecnico.minesocieties.agents.location.LocationReference;
import ulisboa.tecnico.minesocieties.agents.location.SocialLocation;
import ulisboa.tecnico.minesocieties.utils.InstantTypeAdapter;
import ulisboa.tecnico.minesocieties.utils.InventoryTypeAdapter;
import ulisboa.tecnico.minesocieties.visitors.IContextVisitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.logging.Level;

import static ulisboa.tecnico.minesocieties.utils.PromptUtils.*;

/**
 *  Represents all the information that an Agent can have. Accessing an Agent's state should not be done on the
 * Main thread, as it is certain that non-main threads will access it. As such, thread-safety is required, so by
 * accessing an Agent's State with the Main thread, it's at risk of being blocked.
 */
public class AgentState implements IExplainableContext {

    // Private attributes

    private AgentMemory memory;
    private AgentMoods moods = new AgentMoods();
    private AgentPersonalities personalities = new AgentPersonalities();
    private AgentPersona persona;
    private UUID uuid;
    private AgentLocation currentLocation;
    private AgentInventory inventory = new AgentInventory();
    private LocationReference lastVisitedLocation = null;
    private transient boolean dirty = false;
    private final transient Lock stateLock = new ReentrantLock();
    private final transient Lock saveInProgress = new ReentrantLock();

    private static final Gson GSON;

    // Constructors

    public AgentState() {}

    public AgentState(UUID uuid, AgentPersona persona) {
        this.uuid = uuid;
        this.memory = new AgentMemory();
        this.persona = persona;
    }

    // Getters and setters

    public AgentMemory getMemory() {
        return memory;
    }

    public AgentMoods getMoods() {
        return moods;
    }

    public AgentPersonalities getPersonalities() {
        return personalities;
    }

    public AgentPersona getPersona() {
        return persona;
    }

    public AgentLocation getCurrentLocation() {
        return currentLocation;
    }

    public UUID getUUID() {
        return uuid;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public AgentInventory getInventory() {
        return inventory;
    }

    public LocationReference getLastVisitedLocation() {
        return this.lastVisitedLocation;
    }

    public void setLastVisitedLocation(LocationReference lastVisitedLocation) {
        this.lastVisitedLocation = lastVisitedLocation;
    }

    // Other methods

    public void updateCurrentLocation(AnimatedPlayerNPC npc) {
        AgentLocation oldLocation = this.currentLocation;

        this.currentLocation = new AgentLocation(npc.getData().getLocation(), "Current location");

        if (!this.currentLocation.equals(oldLocation)) {
            // Agent's location has changed
            markDirty();
        }
    }


    public void startStateModification() {
        stateLock.lock();
    }

    public void finishStateModification() {
        markDirty();

        stateLock.unlock();
    }

    /**
     *  Gives this agent an initial description. This should only be called when the AgentState is brand new
     * and the agent does not have memories yet. Otherwise, this can mess up the existing memories.
     *  This prompts the LLM into helping to organize the agent's description into opinios, reflections, etc. As such,
     * this is a blocking operation.
     * @param firstMemories
     *  The first memories, in Natural Language, of this agent. This should not include any type of parenthesis
     * '[]', '{}', or the symbol '|', as they are used to instruct the LLM to tidy up the memories.
     * @throws MalformedNewStateResponseException
     *  Thrown if the LLM's response does not conform to the given instructions.
     */
    public void insertDescriptionSync(String firstMemories) throws MalformedNewStateResponseException {
        requestStateChangeSync(getPromptForDescriptionInterpretationSync(firstMemories));
    }

    /**
     *  Gives this agent an initial description. This should only be called when the AgentState is brand new
     * and the agent does not have memories yet. Otherwise, this can mess up the existing memories.
     *  This prompts the LLM into helping to organize the agent's description into opinios, reflections, etc. That's done
     * asynchronously. This method returns immediately.
     * @param firstMemories
     *  The first memories, in Natural Language, of this agent. This should not include any type of parenthesis
     * '[]', '{}', or the symbol '|', as they are used to instruct the LLM to tidy up the memories.
     */
    public void insertDescriptionAsync(String firstMemories) {
        requestStateChangeAsync(() -> getPromptForDescriptionInterpretationSync(firstMemories));
    }

    /**
     *  Called when the state should suffer changes decided by the LLM. Its response will then be interpreted and
     * changes will be applied to this state accordingly. {@link #getNewStateFormat()} should be included in the messages,
     * as it sets the format of the responses that will be interpreted.
     * @param messages
     *  The prompts to be sent to the LLM
     */
    public void requestStateChangeSync(List<LLMMessage> messages) {
        startStateModification();

        try {
            interpretStateResponse(MineSocieties.getPlugin().getLLMManager().promptSync(messages));
        } finally {
            finishStateModification();
        }
    }

    /**
     *  Called when the state should suffer changes decided by the LLM. Its response will then be interpreted and
     * changes will be applied to this state accordingly. {@link #getNewStateFormat()} should be included in the messages,
     * as it sets the format of the responses that will be interpreted.
     * @param messagesSuplier
     *  A supplier for the prompts
     */
    public void requestStateChangeAsync(Supplier<List<LLMMessage>> messagesSuplier) {
        MineSocieties.getPlugin().getLLMManager().promptAsyncSupplyMessageAsync(
                () -> {
                    startStateModification(); // Once the prompt is requested, this state becomes locked

                    return messagesSuplier.get();
                },
                response -> {
                    interpretStateResponse(response);
                    finishStateModification(); // After changes are applied to the state, the state becomes unlocked
                },
                throwable -> finishStateModification() // If there's a problem during prompting, the state becomes unlocked
        );
    }

    private List<LLMMessage> getPromptForDescriptionInterpretationSync(String description) {
        String name = persona.getName();
        List<LLMMessage> messageList = new ArrayList<>(4);

        // Telling the model exactly what to do
        messageList.add(new LLMMessage(LLMRole.SYSTEM,
        "You are a people description analyzer. You will receive people's descriptions " +
                "inside brackets '{}' and must interpret it. " +
                getNewStateFormat())
        );

        // Giving an example of input to the model
        messageList.add(new LLMMessage(LLMRole.USER,
                "Rafael: {Rafael is a software engineer. He loves chocolate. He thinks Francisco is tall, and is weird for not " +
                     "liking pineapple on pizza. Rafael is smart and knows funny dark humour jokes. He's happy with himself. " +
                        "He's going to a party next week. He needs to cook dinner tonight.}"
                )
        );

        // Giving an example of output to the model
        messageList.add(new LLMMessage(LLMRole.ASSISTANT,
                PERSONALITIES_FORMAT_BEGIN + "{engineer|chocolate-lover|smart|good-sense-of-humour}\n" +
                        EMOTIONS_FORMAT_BEGIN + "{happy}\n" +
                        SHORT_MEMORY_FORMAT_BEGIN + "{Rafael needs to cook dinner tonight}\n" +
                        LONG_MEMORY_FORMAT_BEGIN + "{Rafael likes software engineering|Rafael is happy with himself|" +
                        "Rafael thinks that not liking pineapple on pizza is weird|Rafael knows funny dark humour jokes|" +
                        "Rafael knows Francisco does not like pineapple on pizza|" +
                        "Rafael thinks Francisco is tall and weird|Rafael has a party to attend next week}\n" +
                        "Explanation: Since Rafael is a software engineer and he's happy with himself, this means he likes software engineering. " +
                        "Rafael is not tall, however, Francisco is tall according to Rafael. Cooking dinner tonight is something that " +
                        "Rafael needs to remember only for this day, as such, it's considered short-term memory. Rafael is going to a party " +
                        "next week, which is something that he must remember for a longer time, hence, it's considered long-term memory. " +
                        "Rafael thinks Francisco is weird for not liking pineapple on pizza, therefore, he thinks it's weird when someone doesn't " +
                        "like pinneapple on pizza."
                )
        );

        // Giving it the desired input
        messageList.add(new LLMMessage(LLMRole.USER,
                name + ": {" + description + "}"
                )
        );

        return messageList;
    }

    private List<LLMMessage> getPromptForShortLongTermMemoryOptimizationSync() {
        String name = persona.getName();
        List<LLMMessage> messageList = new ArrayList<>(4);

        // Telling the model exactly what to do
        messageList.add(new LLMMessage(LLMRole.SYSTEM,
                "You are a human memory helper. You will receive a list of short-term memories and long-term memories " +
                        "inside brackets '{}' and must optimize them by summarizing them or excluding non-important memories. " +
                        getAdditionalStateFormat()
                )
        );

        // Giving an example of input to the model
        messageList.add(new LLMMessage(LLMRole.USER,
                        "Rafael: {Rafael is a software engineer. He loves chocolate. He thinks Francisco is tall, and is weird for not " +
                                "liking pineapple on pizza. Rafael is smart and knows funny dark humour jokes. He's happy with himself. " +
                                "He's going to a party next week. He needs to cook dinner tonight.}"
                )
        );

        return messageList;
    }

    public String getNewStateFormat() {
        return "Write down the personality traits that best describe them as " + PERSONALITIES_FORMAT +
                " (traits should be a single word or hyphenated words, and they must belong to them), " +
                "and their feelings as " + EMOTIONS_FORMAT + " (single word). " +
                " Record short-term memories as " + SHORT_MEMORY_FORMAT + ". " +
                " Record long-term memories, important details and inferred knowledge as " + LONG_MEMORY_FORMAT +
                ". If a list should be empty, write '{}'. Finally, write a short explanation for your choices.\n";
    }

    public String getAdditionalStateFormat() {
        return "Write down their current personality traits as " + PERSONALITIES_FORMAT +
                " (traits should be a single word or hyphenated words, and they must belong to them), " +
                "which may be the same or may be different ones, " +
                "and their feelings as " + EMOTIONS_FORMAT + " (single word). " +
                " Record new short-term memories as " + SHORT_MEMORY_FORMAT + ". " +
                " Record new long-term memories, important details and inferred knowledge as " + LONG_MEMORY_FORMAT +
                ". If a list should be empty, write '{}'. Finally, write a short explanation for your choices.\n";
    }

    /**
     *  Called when this AgentState should be modified according to an LLM's response to something. This will
     * replace some contents inside this AgentState
     * @param response
     *  The response containing the new information to replace this AgentState
     * @throws MalformedNewStateResponseException
     *  Thrown when the response is not formatted correctly
     */
    public void interpretStateResponse(String response) throws MalformedNewStateResponseException {
        int personalitiesIndex = response.indexOf(PERSONALITIES_FORMAT_BEGIN);
        int emotionsIndex = response.indexOf(EMOTIONS_FORMAT_BEGIN);
        int shortMemoryIndex = response.indexOf(SHORT_MEMORY_FORMAT_BEGIN);
        int longMemoryIndex = response.indexOf(LONG_MEMORY_FORMAT_BEGIN);

        // Checking if the LLM replied with the lists names
        if (personalitiesIndex == -1) {
            throw new MalformedNewStateResponseException("LLM did not create a Personalities list.", response);
        }

        if (emotionsIndex == -1) {
            throw new MalformedNewStateResponseException("LLM did not create an Emotions list.", response);
        }

        if (shortMemoryIndex == -1) {
            throw new MalformedNewStateResponseException("LLM did not create a ShortMemory list.", response);
        }

        if (longMemoryIndex == -1) {
            throw new MalformedNewStateResponseException("LLM did not create a LongMemory list.", response);
        }

        // Interpreting the personalities
        try {
            // The LLM chooses to keep/discard/add new personalities. As such, current ones get reset
            personalities.reset();

            String[] personalities = response.substring(
                    response.indexOf('{', personalitiesIndex) + 1,
                    response.indexOf('}', personalitiesIndex)
            ).split("\\|");

            for (String personality: personalities) {
                if (!personality.isEmpty()) {
                    this.personalities.addState(personality);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new MalformedNewStateResponseException("LLM malformed the Personalities list.", response);
        }

        // Interpreting the emotions
        try {
            // The LLM chooses to keep/discard/add new moods. As such, current ones get reset
            moods.reset();

            String[] emotions = response.substring(
                    response.indexOf('{', emotionsIndex) + 1,
                    response.indexOf('}', emotionsIndex)
            ).split("\\|");

            for (String emotion: emotions) {
                if (!emotion.isEmpty()) {
                    this.moods.addState(emotion);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new MalformedNewStateResponseException("LLM malformed the Emotions list.", response);
        }

        // Interpreting the short term memory
        try {
            String[] shortMemories = response.substring(
                    response.indexOf('{', shortMemoryIndex) + 1,
                    response.indexOf('}', shortMemoryIndex)
            ).split("\\|");

            AgentShortTermMemory agentShortTermMemory = memory.getShortTermMemory();

            for (String shortMemory : shortMemories) {
                if (!shortMemory.isEmpty()) {
                    agentShortTermMemory.addMemorySection(new ShortTermMemorySection(Instant.now(), shortMemory));
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new MalformedNewStateResponseException("LLM malformed the Short-Term Memory list.", response);
        }

        // Interpreting the long term memory
        try {
            String[] longMemories = response.substring(
                    response.indexOf('{', longMemoryIndex) + 1,
                    response.indexOf('}', longMemoryIndex)
            ).split("\\|");

            AgentLongTermMemory agentLongTermMemory = memory.getLongTermMemory();

            for (String longMemory : longMemories) {
                if (!longMemory.isEmpty()) {
                    agentLongTermMemory.addMemorySection(new LongTermMemorySection(Instant.now(), longMemory));
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new MalformedNewStateResponseException("LLM malformed the Long-Term Memory list.", response);
        }
    }

    public Path getStatePath() {
        return getStatePath(uuid, persona.getName());
    }

    public Path getStatePath(UUID uuid, String name) {
        return Path.of(SocialAgentManager.STATES_PATH.toString(), name + "_" + uuid + ".json");
    }

    /**
     *  Saves this current state in its corresponding file
     */
    public void saveSync() {
        try {
            saveInProgress.lock();

            File file = getStatePath().toFile();

            file.getParentFile().mkdirs();
            file.createNewFile();

            Files.write(getStatePath(), GSON.toJson(this).getBytes());

            dirty = false;
        } catch (IOException e) {
            MineSocieties.getPlugin().getLogger().log(Level.WARNING,
                    "Unable to save the state of " + persona.getName() + " into a file.", e);
        } finally {
            saveInProgress.unlock();
        }
    }

    public void saveAsync() {
        MineSocieties.getPlugin().getThreadPool().execute(this::saveSync);
    }

    @Override
    public String accept(IContextVisitor visitor) {
        return visitor.explainState(this);
    }

    // Static methods

    /**
     *  Loads an agent from a file
     * @param pathname
     *  The path name to the file containing the contents of the agent
     * @return
     *  A new AgentState instance corresponding to the saved agent
     * @throws StateReadException
     *  Thrown if something goes wrong while reading the file containing the agent's contents
     */
    public static AgentState load(String pathname) throws StateReadException {
        try {
            String contents = Files.readString(Path.of(pathname));

            return GSON.fromJson(contents, AgentState.class);
        } catch (IOException | JsonSyntaxException e) {
            throw new StateReadException("Unable to load the agent stored under " + pathname, e);
        }
    }

    public static Gson getGson() {
        return GSON;
    }

    // Static
    static {
        // Creating the builder for the Gson that will serialize and deserialize an AgentState
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.setPrettyPrinting();

        gsonBuilder.registerTypeAdapter(Instant.class, new InstantTypeAdapter());
        gsonBuilder.registerTypeAdapter(Inventory.class, new InventoryTypeAdapter());

        GSON = gsonBuilder.create();
    }

    /**
     * @return
     *  Returns all the locations that this agent knows about
     */
    public List<LocationReference> getAllLocations() {
        List<LocationReference> locations = new ArrayList<>();

        locations.add(memory.getHome());
        locations.addAll(memory.getKnownLocations().getMemorySections());

        return locations;
    }

    public void markDirty() {
        dirty = true;
    }

    // Classes

    public static class StateReadException extends Exception {

        // Constructors

        public StateReadException(String message, Exception cause) {
            super(message, cause);
        }
    }
}
