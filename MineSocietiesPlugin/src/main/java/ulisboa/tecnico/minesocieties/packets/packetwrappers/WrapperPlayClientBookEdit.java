/**
 * This file is part of PacketWrapper.
 * Copyright (C) 2012-2015 Kristian S. Strangeland
 * Copyright (C) 2015 dmulloy2
 *
 * PacketWrapper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PacketWrapper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with PacketWrapper.  If not, see <http://www.gnu.org/licenses/>.
 */
package ulisboa.tecnico.minesocieties.packets.packetwrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

import com.comphenix.protocol.reflect.EquivalentConverter;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.comphenix.protocol.wrappers.Converters;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WrapperPlayClientBookEdit extends AbstractPacket {

    public static final PacketType TYPE = PacketType.Play.Client.B_EDIT;

    public WrapperPlayClientBookEdit() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayClientBookEdit(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getSlot() {
        return handle.getIntegers().read(0);
    }

    /**
     * Retrieve this sign's lines of text.
     *
     * @return The current lines
     */
    public List<String> getPages() {
        return handle.getModifier().withType(
                List.class,
                new EquivalentConverter<List<String>>() {
                    @Override
                    public Object getGeneric(List<String> specific) {
                        return specific;
                    }

                    @Override
                    public List<String> getSpecific(Object generic) {
                        return (List<String>) generic;
                    }

                    @Override
                    public Class<List<String>> getSpecificType() {
                        Class<?> dummy = List.class;

                        return (Class<List<String>>) dummy;
                    }
                }
        ).read(0);
    }

    /**
     * Retrieve Is signing.
     * <p>
     * Notes: true if the player is signing the book; false if the player is saving a draft.
     * @return The current Is signing
     */
    public boolean getIsSigning() {
        return handle.getBooleans().read(0);
    }

    /**
     * Set Is signing.
     * @param value - new value.
     */
    public void setIsSigning(boolean value) {
        handle.getBooleans().write(0, value);
    }
}