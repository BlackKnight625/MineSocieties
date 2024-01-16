package ulisboa.tecnico.minesocieties;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.entityutils.entity.event.EventManager;
import ulisboa.tecnico.agents.ExampleReactiveAgentManager;
import ulisboa.tecnico.llms.ChatGPTManager;
import ulisboa.tecnico.llms.LLMManager;
import ulisboa.tecnico.minesocieties.agents.SocialAgentManager;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.commands.CommandManager;

import java.util.concurrent.ExecutorService;

public class MineSocieties extends JavaPlugin {

    // Private attributes

    private static MineSocieties PLUGIN;

    private ExampleReactiveAgentManager reactiveAgentManager;
    private SocialAgentManager socialAgentManager;
    private LLMManager llmManager;
    private long elapsedTicks;
    private int maxChatRange;
    private boolean chatBroadcast;
    private boolean loadSavedAgents;

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


        reactiveAgentManager = new ExampleReactiveAgentManager(this);

        reactiveAgentManager.initialize();

        socialAgentManager = new SocialAgentManager(this);

        socialAgentManager.initialize();

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

        if (loadSavedAgents) {
            // Loading all agents in the next tick
            new BukkitRunnable() {
                @Override
                public void run() {
                    socialAgentManager.loadSavedAgents();
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
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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

    public ExecutorService getThreadPool() {
        // Sharing the same thread pool as the llmManager's
        return llmManager.getThreadPool();
    }

    public long getElapsedTicks() {
        return elapsedTicks;
    }

    public static MineSocieties getPlugin() {
        return PLUGIN;
    }

    public static void setPlugin(MineSocieties PLUGIN) {
        MineSocieties.PLUGIN = PLUGIN;
    }
}
