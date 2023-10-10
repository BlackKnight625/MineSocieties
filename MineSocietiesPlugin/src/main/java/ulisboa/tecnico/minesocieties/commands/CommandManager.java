package ulisboa.tecnico.minesocieties.commands;

import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitCommandHandler;

public class CommandManager {

    public CommandManager(JavaPlugin plugin) {
        BukkitCommandHandler commandHandler = BukkitCommandHandler.create(plugin);

        commandHandler.registerBrigadier();

        commandHandler.register(new ReactiveAgentCommand());
        commandHandler.register(new SocialAgentCommand());
    }
}
