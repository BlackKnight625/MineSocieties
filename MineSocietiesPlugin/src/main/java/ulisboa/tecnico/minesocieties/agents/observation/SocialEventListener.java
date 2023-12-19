package ulisboa.tecnico.minesocieties.agents.observation;

import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.EntitiesLoadEvent;
import ulisboa.tecnico.agents.observation.EventListener;
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
                }
            }
        }
    }
}
