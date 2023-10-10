package ulisboa.tecnico.minesocieties.commands;

import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import ulisboa.tecnico.minesocieties.MineSocieties;

@Command("agent reactive")
public class ReactiveAgentCommand {

    @Subcommand("deploy")
    public void deployAgent(Player player, String agentName) {
        MineSocieties.getPlugin().getReactiveAgentManager().deployAgent(agentName, player.getLocation());
    }
}
