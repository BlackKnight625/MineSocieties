package ulisboa.tecnico.minesocieties.packets.packetwrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;

/**
 *  Credits to <a href="https://github.com/dmulloy2/PacketWrapper">...</a>
 */
public class WrapperPlayServerOpenSignEditor extends AbstractPacket {

    public static final PacketType TYPE =
            PacketType.Play.Server.OPEN_SIGN_EDITOR;

    public WrapperPlayServerOpenSignEditor() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerOpenSignEditor(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve Location.
     *
     * @return The current Location
     */
    public BlockPosition getLocation() {
        return handle.getBlockPositionModifier().read(0);
    }

    /**
     * Set Location.
     *
     * @param value - new value.
     */
    public void setLocation(BlockPosition value) {
        handle.getBlockPositionModifier().write(0, value);
    }

}