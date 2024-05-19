package ulisboa.tecnico.minesocieties.commands;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.autocomplete.AutoCompleter;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.command.ExecutableCommand;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.SocialAgentManager;
import ulisboa.tecnico.minesocieties.agents.SocialCharacter;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.utils.LocationUtils;

import java.util.*;

public class CommandManager {

    public CommandManager(JavaPlugin plugin) {
        BukkitCommandHandler commandHandler = BukkitCommandHandler.create(plugin);

        commandHandler.registerBrigadier();

        // Value resolvers
        commandHandler.registerValueResolver(SocialAgent.class, context -> getAgentFromName(context.pop()));
        commandHandler.registerValueResolver(SocialPlayer.class, context -> getPlayerFromName(context.pop()));

        AutoCompleter autoCompleter = commandHandler.getAutoCompleter();

        // Auto completions
        autoCompleter.registerSuggestion("closeAgentsToChat", (args, sender, command) -> {
            if (sender instanceof BukkitCommandActor bukkitCommandActor) {
                // Returning the names of nearby agents between quotation marks
                Player player = bukkitCommandActor.requirePlayer();
                return withQuotationMarks(LocationUtils.getNearbyAgentNames(player.getLocation()));
            } else {
                return Collections.emptyList();
            }
        });

        autoCompleter.registerSuggestion("backups", (args, sender, command) -> MineSocieties.getPlugin().listBackups());

        autoCompleter.registerParameterSuggestions(SocialAgent.class, (args, sender, command) -> {
            List<String> names = new LinkedList<>();

            MineSocieties.getPlugin().getSocialAgentManager().forEachValidAgent(agent -> names.add(agent.getName()));

            return withQuotationMarks(names);
        });

        commandHandler.register(new ReactiveAgentCommand());
        commandHandler.register(new SocialAgentCommand());
        commandHandler.register(new DebugCommand());
    }

    private List<String> withQuotationMarks(List<String> input) {
        return input.stream().map(name -> "\"" + name + "\"").toList();
    }

    public static @Nullable SocialCharacter getCharacterFromName(String name) {
        name = name.replaceAll("\"", "");

        return MineSocieties.getPlugin().getSocialAgentManager().getCharacter(name);
    }

    public static @Nullable SocialAgent getAgentFromName(String name) {
        SocialCharacter socialCharacter = getCharacterFromName(name);

        if (socialCharacter instanceof SocialAgent socialAgent) {
            return socialAgent;
        } else {
            return null;
        }
    }

    public static @Nullable SocialPlayer getPlayerFromName(String name) {
        SocialCharacter socialCharacter = getCharacterFromName(name);

        if (socialCharacter instanceof SocialPlayer socialPlayer) {
            return socialPlayer;
        } else {
            return null;
        }
    }
}
