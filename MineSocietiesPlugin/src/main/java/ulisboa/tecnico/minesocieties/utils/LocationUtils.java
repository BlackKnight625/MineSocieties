package ulisboa.tecnico.minesocieties.utils;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.SocialAgentManager;
import ulisboa.tecnico.minesocieties.agents.npc.SocialAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LocationUtils {

    public static boolean isClose(Location location1, Location location2, double radius) {
        if (location1.getWorld().equals(location2.getWorld())) {
            return location1.distanceSquared(location2) <= radius * radius;
        } else {
            return false;
        }
    }

    public static boolean isCloseForChatting(Location location1, Location location2) {
        return isClose(location1, location2, MineSocieties.getPlugin().getMaxChatRange());
    }

    public static List<String> getNearbyCharacterNamesExcludingSelf(Location location, UUID self) {
        List<String> names = getNearbyAgentNamesExcludingSelf(location, self);

        int maxChatRange = MineSocieties.getPlugin().getMaxChatRange();

        for (Entity entity : location.getWorld().getNearbyEntities(location, maxChatRange, maxChatRange, maxChatRange)) {
            if (entity instanceof Player other && !self.equals(other.getUniqueId()) && other.getGameMode() != GameMode.SPECTATOR) {
                // Found a nearby player
                names.add(other.getName());
            }
        }

        return names;
    }

    public static List<String> getNearbyAgentNames(Location location) {
        List<String> names = new ArrayList<>();

        MineSocieties.getPlugin().getSocialAgentManager().forEachValidAgent(agent -> {
            if (isCloseForChatting(location, agent.getLocation())) {
                // Found a nearby agent
                names.add(agent.getName());
            }
        });

        return names;
    }

    public static List<String> getNearbyAgentNamesExcludingSelf(Location location, UUID self) {
        List<String> names = new ArrayList<>();

        MineSocieties.getPlugin().getSocialAgentManager().forEachValidAgent(agent -> {
            if (!self.equals(agent.getUUID()) && isCloseForChatting(location, agent.getLocation())) {
                // Found a nearby agent
                names.add(agent.getName());
            }
        });

        return names;
    }
}
