package ulisboa.tecnico.minesocieties;

import org.bukkit.plugin.java.JavaPlugin;
import ulisboa.tecnico.chatgpt.ChatGPTManager;

public final class MineSocieties extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        new ChatGPTManager();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
