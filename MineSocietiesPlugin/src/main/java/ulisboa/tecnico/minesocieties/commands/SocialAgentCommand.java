package ulisboa.tecnico.minesocieties.commands;

import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.exception.CommandErrorException;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;

@Command("agent")
public class SocialAgentCommand {

    @Subcommand("deploy")
    @Description("Deploys a Social Agent at the player's current location. The player must specify the agent's name " +
            "between quotation marks (Ex: \"John Smith\"). After specifying the name, the player can optionally give " +
            "the agent an initial description in natural language. This should not be between quotation marks (Ex: " +
            "John Smith is an intelligent man. He likes Alicia. He studied maths and physics. He hates geometry.)")
    @AutoComplete("\"\"") // Suggesting quotation marks so the player puts the agent's name inside them
    public void deployAgent(Player player, String agentName, @Optional String description) {
        try {
            MineSocieties.getPlugin().getSocialAgentManager().deployAgent(agentName, player.getLocation(), description);
        } catch (IllegalArgumentException e) {
            throw new CommandErrorException("An error occurred while deploying a social agent: " + e.getMessage());
        }
    }

    @Subcommand("talk")
    @AutoComplete("@closeAgentsToChat")
    public void talkWith(Player player, SocialAgent who, String message) {
        MineSocieties.getPlugin().getSocialAgentManager().talkWith(getSocialPlayer(player), who, message);
    }

    private SocialPlayer getSocialPlayer(Player player) {
        return MineSocieties.getPlugin().getSocialAgentManager().getPlayerWrapper(player);
    }
}
