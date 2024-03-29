package ulisboa.tecnico.minesocieties.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.inventory.Inventory;

import java.io.IOException;

public class InventoryTypeAdapter extends TypeAdapter<Inventory> {
    @Override
    public void write(JsonWriter out, Inventory value) throws IOException {
        out.value(BukkitSerialization.toBase64(value));
    }

    @Override
    public Inventory read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        return BukkitSerialization.fromBase64(in.nextString());
    }
}
