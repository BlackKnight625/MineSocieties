package ulisboa.tecnico.minesocieties.commands;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.AutoCompleter;
import revxrsal.commands.autocomplete.SuggestionProvider;
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
        commandHandler.registerValueResolver(SocialAgent.class, context -> {
            String name = context.pop();

            name = name.replaceAll("\"", "");

            SocialCharacter socialCharacter = MineSocieties.getPlugin().getSocialAgentManager().getCharacter(name);

            if (socialCharacter instanceof SocialAgent socialAgent) {
                return socialAgent;
            } else {
                return null;
            }
        });

        commandHandler.registerValueResolver(SocialPlayer.class, context -> {
            String name = context.pop();

            name = name.replaceAll("\"", "");

            SocialCharacter socialCharacter = MineSocieties.getPlugin().getSocialAgentManager().getCharacter(name);

            if (socialCharacter instanceof SocialPlayer socialPlayer) {
                return socialPlayer;
            } else {
                return null;
            }
        });

        AutoCompleter autoCompleter = commandHandler.getAutoCompleter();

        // Auto completions
        autoCompleter.registerSuggestion("closeAgentsToChat", (args, sender, command) -> {
            if (sender instanceof Player player) {
                // Returning the names of nearby agents between quotation marks
                return withQuotationMarks(LocationUtils.getNearbyAgentNames(player.getLocation()));
            } else {
                return Collections.emptyList();
            }
        });

        autoCompleter.registerParameterSuggestions(SocialAgent.class, (args, sender, command) -> {
            List<String> names = new LinkedList<>();

            MineSocieties.getPlugin().getSocialAgentManager().forEachValidAgent(agent -> names.add(agent.getName()));

            return withQuotationMarks(names);
        });

        commandHandler.register(new ReactiveAgentCommand());
        commandHandler.register(new SocialAgentCommand());
    }

    private List<String> withQuotationMarks(List<String> input) {
        return input.stream().map(name -> "\"" + name + "\"").toList();
    }
}
