package ulisboa.tecnico.minesocieties.agents.observation;

import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import ulisboa.tecnico.agents.observation.EventListener;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.SocialAgentManager;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;

import java.util.UUID;

public class SocialEventListener extends EventListener {

    // Constructors

    public SocialEventListener(SocialAgentManager manager) {
        super(manager);
    }

    // Getters and setters

    @Override
    public SocialAgentManager getManager() {
        return (SocialAgentManager) super.getManager();
    }

    // Event listeners

    @EventHandler
    public void onEntitiesLoad(EntitiesLoadEvent e) {
        for (Entity entity : e.getEntities()) {
            UUID socialPlayerUuid = SocialAgent.getUuidFromContainer(entity.getPersistentDataContainer());

            if (socialPlayerUuid != null) {
                // This entity belongs to a Social NPC
                SocialAgent socialAgent = getManager().getAgent(socialPlayerUuid);

                if (socialAgent == null) {
                    // This agent doesn't exist
                    if (entity instanceof TextDisplay) {
                        // This used to be a part of an agent's Message Display
                        entity.remove();
                    }
                } else {
                    if (entity instanceof TextDisplay textDisplay) {
                        // This is an agent's Message Display
                        socialAgent.getMessageDisplay().setTextDisplay(textDisplay);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                MineSocieties.getPlugin().getSocialAgentManager().addPlayerAsViewer(e.getPlayer());
            }
        }.runTask(MineSocieties.getPlugin());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                MineSocieties.getPlugin().getSocialAgentManager().removePlayerAsViewer(e.getPlayer());
            }
        }.runTask(MineSocieties.getPlugin());
    }
}
