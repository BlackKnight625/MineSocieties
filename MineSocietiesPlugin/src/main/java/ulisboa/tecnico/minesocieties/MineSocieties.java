package ulisboa.tecnico.minesocieties;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.entityutils.entity.event.EventManager;
import ulisboa.tecnico.agents.ExampleReactiveAgentManager;
import ulisboa.tecnico.llms.ChatGPTManager;
import ulisboa.tecnico.llms.LLMManager;
import ulisboa.tecnico.minesocieties.agents.SocialAgentManager;
import ulisboa.tecnico.minesocieties.agents.location.LocationsManager;
import ulisboa.tecnico.minesocieties.agents.npc.Message;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.commands.CommandManager;
import ulisboa.tecnico.minesocieties.guis.GuiManager;
import ulisboa.tecnico.minesocieties.packets.PacketManager;
import ulisboa.tecnico.minesocieties.visitors.CurrentActionExplainer;
import ulisboa.tecnico.minesocieties.visitors.IActionVisitor;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class MineSocieties extends JavaPlugin {

    // Private attributes

    private static MineSocieties PLUGIN;

    private ExampleReactiveAgentManager reactiveAgentManager;
    private SocialAgentManager socialAgentManager;
    private LocationsManager locationsManager;
    private LLMManager llmManager;
    private GuiManager guiManager;
    private PacketManager packetManager;
    private long elapsedTicks;
    private int maxChatRange;
    private boolean chatBroadcast;
    private boolean loadSavedAgents;
    private boolean agentsCanChooseActions;
    private boolean showWhatAgentsAreDoing;
    private boolean showThoughts;
    private int showThoughtsTicks;
    private double locationBoundActionRange;
    private double locationBoundActionRangeSquared;
    private boolean debugMode;

    // Other methods

    @Override
    public void onEnable() {
        saveDefaultConfig();

        String modelOwner = getConfig().getString("llmModel");
        String apiKey = getConfig().getString("openAI_API_key");
        String model = getConfig().getString("chatGPTModel");

        PLUGIN = this;

        // Entity Utils start-up logic
        this.getServer().getPluginManager().registerEvents(new EventManager(), this);

        // Plugin startup logic


        // reactiveAgentManager = new ExampleReactiveAgentManager(this);

        // reactiveAgentManager.initialize();

        socialAgentManager = new SocialAgentManager(this);

        socialAgentManager.initialize();

        locationsManager = new LocationsManager();

        new CommandManager(this);

        // Deciding which LLM Manager should be used
        if (modelOwner.equalsIgnoreCase("OpenAI")) {
            llmManager = new ChatGPTManager(apiKey, model, getLogger(), getLogger() /*TODO Replace with Logger that has its own file*/);
        }

        if (llmManager == null) {
            throw new RuntimeException("The Large Language Model in the config was not correctly specified. Model Owner '" +
                modelOwner + "' is unknown.");
        } else {
            llmManager.initialize();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                elapsedTicks++;
            }
        }.runTaskTimer(this, 0, 1);

        getLogger().info("MineSocieties is enabled!");

        // Loading other config values

        maxChatRange = getConfig().getInt("maxChatRange");
        chatBroadcast = getConfig().getBoolean("chatBroadcast");
        loadSavedAgents = getConfig().getBoolean("loadSavedAgents");
        agentsCanChooseActions = getConfig().getBoolean("agentsCanStartChoosingActionsByDefault");
        showWhatAgentsAreDoing = getConfig().getBoolean("showWhatAgentsAreDoing");
        showThoughts = getConfig().getBoolean("showThoughts");
        showThoughtsTicks = getConfig().getInt("showThoughtsTicks");
        locationBoundActionRange = getConfig().getDouble("locationBoundActionRange");
        debugMode = getConfig().getBoolean("debug");

        locationBoundActionRangeSquared = locationBoundActionRange * locationBoundActionRange;

        if (loadSavedAgents) {
            // Loading all agents in the next tick
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        socialAgentManager.loadSavedAgents();
                        locationsManager.loadSync();
                    } catch (IOException e) {
                        //  Something critical happened while loading the agents. The server should be stopped
                        // since a missing agent or location could cause side effects on other agent's memories
                        getLogger().severe("Error loading saved agents and/or agent locations. Stopping the server.");
                        e.printStackTrace();

                        getServer().shutdown();
                    }

                    // Some locations might have been deleted or some agents may no longer exist. Removing invalid instances
                    locationsManager.checkAndDeleteInvalidLocationsSync();
                    socialAgentManager.deleteAgentsInvalidLocations();
                }
            }.runTask(this);
        }

        // Ticking all agents
        new BukkitRunnable() {
            @Override
            public void run() {
                socialAgentManager.forEachValidAgent(SocialAgent::tick);
            }
        }.runTaskTimer(this, 1, 1);

        // Saving agents periodically
        new BukkitRunnable() {
            @Override
            public void run() {
                getThreadPool().execute(() -> {
                    saveDirtyAgentsAsync();
                });
            }
        }.runTaskTimerAsynchronously(this, 20, 10 * 20);

        if (showWhatAgentsAreDoing) {
            // Showing a message on top of agent's heads indicating what they are doing
            new BukkitRunnable() {
                IActionVisitor currentActionExplainer = new CurrentActionExplainer();

                @Override
                public void run() {
                    socialAgentManager.forEachValidAgent(agent -> {
                        Component text = Component.text("\u26F3 ") // ⛳
                                .color(TextColor.color(98, 210, 89))
                                .append(Component.text(agent.getCurrentAction().accept(currentActionExplainer)).color(TextColor.color(180, 180, 180)));

                        agent.getMessageDisplay().displayMessage(new Message(20, text));
                    });
                }
            }.runTaskTimer(this, 0, 20);
        }

        // Initializing managers
        guiManager = new GuiManager(this);
        packetManager = new PacketManager(this);
    }

    public void saveDirtyAgentsAsync() {
        socialAgentManager.forEachAgent(agent -> {
            if (agent.getState().isDirty()) {
                agent.getState().saveAsync();
            }
        });
    }

    public void saveDirtyAgentsSync() {
        socialAgentManager.forEachAgent(agent -> {
            if (agent.getState().isDirty()) {
                agent.getState().saveSync();
            }
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveDirtyAgentsSync();
    }

    public ExampleReactiveAgentManager getReactiveAgentManager() {
        return reactiveAgentManager;
    }

    public void setReactiveAgentManager(ExampleReactiveAgentManager reactiveAgentManager) {
        this.reactiveAgentManager = reactiveAgentManager;
    }

    public SocialAgentManager getSocialAgentManager() {
        return socialAgentManager;
    }

    public LocationsManager getLocationsManager() {
        return locationsManager;
    }

    public void setSocialAgentManager(SocialAgentManager socialAgentManager) {
        this.socialAgentManager = socialAgentManager;
    }

    public LLMManager getLLMManager() {
        return llmManager;
    }

    public void setLLMManager(LLMManager llmManager) {
        this.llmManager = llmManager;
    }

    public int getMaxChatRange() {
        return maxChatRange;
    }

    public boolean isChatBroadcasted() {
        return chatBroadcast;
    }

    public boolean loadSavedAgents() {
        return loadSavedAgents;
    }

    public boolean showThoughts() {
        return this.showThoughts;
    }

    public int getShowThoughtsTicks() {
        return this.showThoughtsTicks;
    }

    public ExecutorService getThreadPool() {
        // Sharing the same thread pool as the llmManager's
        return llmManager.getThreadPool();
    }

    public long getElapsedTicks() {
        return elapsedTicks;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public PacketManager getPacketManager() {
        return packetManager;
    }

    public boolean agentsCanChooseActions() {
        return agentsCanChooseActions;
    }

    public void setAgentsCanChooseActions(boolean agentsCanChooseActions) {
        this.agentsCanChooseActions = agentsCanChooseActions;
    }

    public boolean showWhatAgentsAreDoing() {
        return showWhatAgentsAreDoing;
    }

    public double getLocationBoundActionRange() {
        return locationBoundActionRange;
    }

    public double getLocationBoundActionRangeSquared() {
        return locationBoundActionRangeSquared;
    }

    public boolean isDebugMode() {
        return this.debugMode;
    }

    public static MineSocieties getPlugin() {
        return PLUGIN;
    }

    public static void setPlugin(MineSocieties PLUGIN) {
        MineSocieties.PLUGIN = PLUGIN;
    }
}
