package ulisboa.tecnico.minesocieties.commands;

import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.exception.CommandErrorException;
import ulisboa.tecnico.minesocieties.MineSocieties;

@Command("agent reactive")
public class ReactiveAgentCommand {

    @Subcommand("deploy")
    public void deployAgent(Player player, String agentName) {
        try {
            MineSocieties.getPlugin().getReactiveAgentManager().deployAgent(agentName, player.getLocation());
        } catch (IllegalArgumentException e) {
            throw new CommandErrorException("An error occurred while deploying a reactive agent: " + e.getMessage());
        }
    }
}
