package ulisboa.tecnico.minesocieties;

import org.bukkit.plugin.java.JavaPlugin;
import org.entityutils.entity.event.EventManager;
import ulisboa.tecnico.agents.ExampleReactiveAgentManager;
import ulisboa.tecnico.chatgpt.ChatGPTManager;
import ulisboa.tecnico.minesocieties.commands.CommandManager;

public final class MineSocieties extends JavaPlugin {

    // Private attributes

    private static MineSocieties PLUGIN;

    private ExampleReactiveAgentManager reactiveAgentManager;

    // Other methods

    @Override
    public void onEnable() {
        PLUGIN = this;

        // Entity Utils start-up logic
        this.getServer().getPluginManager().registerEvents(new EventManager(), this);

        // Plugin startup logic
        reactiveAgentManager = new ExampleReactiveAgentManager(this);

        reactiveAgentManager.initialize();

        new CommandManager(this);
        new ChatGPTManager();

        getLogger().info("MineSocieties is enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ExampleReactiveAgentManager getReactiveAgentManager() {
        return reactiveAgentManager;
    }

    public static MineSocieties getPlugin() {
        return PLUGIN;
    }
}
