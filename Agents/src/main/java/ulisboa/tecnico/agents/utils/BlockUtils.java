package ulisboa.tecnico.agents.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.LinkedList;
import java.util.List;

public class BlockUtils {

    /**
     *  Get the blocks in a radius around a location
     * @param location
     *  The given location
     * @param radius
     *  The radius centered in the given location
     * @return
     *  The list of blocks in the radius around the given location
     */
    public static List<Block> getNearbyBlocks(Location location, int radius) {
        return getBlocksBetween(location.clone().add(-radius, -radius, -radius),
                location.clone().add(radius, radius, radius));
    }

    /**
     *  Get the blocks between two locations
     * @param location1
     *  The first location
     * @param location2
     *  The second location
     * @return
     *  The list of blocks between the two given locations
     */
    public static List<Block> getBlocksBetween(Location location1, Location location2) {
        int minX = Math.min(location1.getBlockX(), location2.getBlockX());
        int minY = Math.min(location1.getBlockY(), location2.getBlockY());
        int minZ = Math.min(location1.getBlockZ(), location2.getBlockZ());
        int maxX = Math.max(location1.getBlockX(), location2.getBlockX());
        int maxY = Math.max(location1.getBlockY(), location2.getBlockY());
        int maxZ = Math.max(location1.getBlockZ(), location2.getBlockZ());

        // Creating a LinkedList instead of a big ArrayList since most of the time, filters will be applied to the list,
        // and removing elements from an ArrayList is O(n) while removing elements from a LinkedList is O(1)
        List<Block> blocks = new LinkedList<>();

        World world = location1.getWorld();

        if (!world.equals(location2.getWorld())) {
            throw new IllegalArgumentException("Locations must be in the same world. Location1's world: " + world +
                    " Location2's world: " + location2.getWorld() + ".");
        }

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    blocks.add(world.getBlockAt(x, y, z));
                }
            }
        }

        return blocks;
    }
}
