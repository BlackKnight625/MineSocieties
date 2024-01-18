package ulisboa.tecnico.minesocieties.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import ulisboa.tecnico.minesocieties.MineSocieties;
import ulisboa.tecnico.minesocieties.agents.player.SocialPlayer;
import ulisboa.tecnico.minesocieties.packets.packetwrappers.*;

import java.util.List;

public class PacketManager {

    // Private attributes
    private final ProtocolManager manager = ProtocolLibrary.getProtocolManager();

    // Constructors

    public PacketManager(MineSocieties plugin) {
        // Credits to https://www.spigotmc.org/threads/sign-gui-for-user-input.104394/
        manager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.UPDATE_SIGN) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                SocialPlayer player = MineSocieties.getPlugin().getSocialAgentManager().getPlayerWrapper(event.getPlayer());

                if (player.isEditingCustomSign()) {
                    // Player is editing a custom sign
                    MineSocieties.getPlugin().getGuiManager().signChanged(player, new WrapperPlayClientUpdateSign(event.getPacket()).getLines());
                    player.setEditingCustomSign(false);

                    // Changing the sign back to its previous block
                    WrapperPlayServerBlockChange blockPacket = new WrapperPlayServerBlockChange();
                    Location location = player.getPlayer().getLocation();
                    BlockPosition blockPosition = new BlockPosition(location.getBlockX(), location.getBlockY() + 3, location.getBlockZ());

                    blockPacket.setLocation(blockPosition);
                    blockPacket.setBlockData(WrappedBlockData.createData(location.getBlock().getBlockData()));

                    blockPacket.sendPacket(player.getPlayer());
                }
            }

            @Override
            public void onPacketSending(PacketEvent event) {

            }
        });

        manager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.B_EDIT) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                SocialPlayer player = MineSocieties.getPlugin().getSocialAgentManager().getPlayerWrapper(event.getPlayer());
                WrapperPlayClientBookEdit bookPacket = new WrapperPlayClientBookEdit(event.getPacket());
                int slot = bookPacket.getSlot();
                ItemStack book = player.getPlayer().getInventory().getItem(slot);
                List<String> pages = bookPacket.getPages();

                MineSocieties.getPlugin().getGuiManager().bookChanged(player, book, pages);
            }

            @Override
            public void onPacketSending(PacketEvent event) {

            }
        });
    }

    // Other methods

    public void openSignEditor(SocialPlayer player) {
        Location location = player.getPlayer().getLocation();
        WrapperPlayServerOpenSignEditor signPacket = new WrapperPlayServerOpenSignEditor();
        WrapperPlayServerBlockChange blockPacket = new WrapperPlayServerBlockChange();
        // Changing a nearby block into a sign
        BlockPosition blockPosition = new BlockPosition(location.getBlockX(), location.getBlockY() + 3, location.getBlockZ());

        signPacket.setLocation(blockPosition);
        blockPacket.setLocation(blockPosition);
        blockPacket.setBlockData(WrappedBlockData.createData(Material.OAK_SIGN));

        player.setEditingCustomSign(true);

        blockPacket.sendPacket(player.getPlayer());
        signPacket.sendPacket(player.getPlayer());
    }
}
