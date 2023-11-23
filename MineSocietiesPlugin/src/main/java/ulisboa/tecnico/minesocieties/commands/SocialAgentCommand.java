package ulisboa.tecnico.minesocieties.commands;

import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.exception.CommandErrorException;
import ulisboa.tecnico.minesocieties.MineSocieties;

@Command("agent")
public class SocialAgentCommand {

    @Subcommand("deploy")
    @Description("Deploys a Social Agent at the player's current location. The player must specify the agent's name " +
            "between quotation marks (Ex: \"John Smith\"). After specifying the name, the player can optionally give " +
            "the agent an initial description in natural language. This should not be between quotation marks (Ex: " +
            "John Smith is an intelligent man. He likes Alicia. He studied maths and physics. He hates geometry.)")
    public void deployAgent(Player player, String agentName, @Optional String description) {
        try {
            MineSocieties.getPlugin().getSocialAgentManager().deployAgent(agentName, player.getLocation(), description);
        } catch (IllegalArgumentException e) {
            throw new CommandErrorException("An error occurred while deploying a social agent: " + e.getMessage());
        }
    }
}
