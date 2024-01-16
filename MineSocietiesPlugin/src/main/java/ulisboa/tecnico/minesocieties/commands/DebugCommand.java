package ulisboa.tecnico.minesocieties.commands;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;

@Command("socdebug")
public class DebugCommand {

    @Subcommand("sign")
    @CommandPermission("minesocieties.debug")
    public void openSignEditor(Player player) {
        MineSocieties plugin = MineSocieties.getPlugin();
        SocialPlayer socialPlayer = plugin.getSocialAgentManager().getPlayerWrapper(player);

        plugin.getGuiManager().openSignGUI(socialPlayer, lines -> new BukkitRunnable() {
            @Override
            public void run() {
                for(String line : lines) {
                    socialPlayer.getPlayer().sendMessage(line);
                }
            }
        }.runTask(plugin));
    }
}
