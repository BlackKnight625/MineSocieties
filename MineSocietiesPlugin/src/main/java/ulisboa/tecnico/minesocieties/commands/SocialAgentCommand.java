package ulisboa.tecnico.minesocieties.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.exception.CommandErrorException;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.guis.social.locations.AllLocationsMenu;
import ulisboa.tecnico.minesocieties.utils.ComponentUtils;

@Command("agent")
public class SocialAgentCommand {

    @Subcommand("deploy")
    @Description("Deploys a Social Agent at the player's current location. The player must specify the agent's name " +
            "between quotation marks (Ex: \"John Smith\"). After specifying the name, the player can optionally give " +
            "the agent an initial description in natural language. This should not be between quotation marks (Ex: " +
            "John Smith is an intelligent man. He likes Alicia. He studied maths and physics. He hates geometry.)")
    @AutoComplete("\"\"") // Suggesting quotation marks so the player puts the agent's name inside them
    @CommandPermission("minesocieties.deploy")
    public void deployAgent(Player player, String agentName, @Optional String description) {
        try {
            MineSocieties.getPlugin().getSocialAgentManager().deployNewAgent(agentName, player.getLocation(), description);
            MineSocieties.getPlugin().getGuiManager().giveNPCStickIfNotPresent(getSocialPlayer(player));
        } catch (IllegalArgumentException e) {
            throw new CommandErrorException("An error occurred while deploying a social agent: " + e.getMessage());
        }
    }

    @Subcommand("talk")
    @AutoComplete("@closeAgentsToChat")
    @CommandPermission("minesocieties.talk")
    public void talkWith(Player player, SocialAgent who, String message) {
        MineSocieties.getPlugin().getSocialAgentManager().talkWith(getSocialPlayer(player), who, message);
    }

    @Subcommand("debug")
    @CommandPermission("minesocieties.debug")
    public void debug1(Player player) {
        MineSocieties.getPlugin().getSocialAgentManager().forEachValidAgent(a -> {
            System.out.println("Name: " + a.getName() + ", UUID: " + a.getUUID());
        });
    }

    @Subcommand("edit_stick")
    @CommandPermission("minesocieties.edit")
    public void editStick(Player player) {
        MineSocieties.getPlugin().getGuiManager().giveNPCStick(getSocialPlayer(player));
    }

    @Subcommand("allow_action_choice")
    @CommandPermission("minesocieties.admin")
    public void allowActionChoice(Player player, boolean allow) {
        MineSocieties.getPlugin().setAgentsCanChooseActions(allow);

        if (allow) {
            player.sendMessage(ComponentUtils.withPrefix(Component.text("Social agents can now choose their actions.").color(TextColor.color(53, 229, 54))));
        } else {
            player.sendMessage(ComponentUtils.withPrefix(Component.text("Social agents can no longer choose their actions.").color(TextColor.color(229, 68, 29))));
        }
    }

    @Subcommand("locations")
    @CommandPermission("minesocieties.admin")
    public void openLocationsMenu(Player player) {
        new AllLocationsMenu(getSocialPlayer(player)).open();
    }

    private SocialPlayer getSocialPlayer(Player player) {
        return MineSocieties.getPlugin().getSocialAgentManager().getPlayerWrapper(player);
    }
}
