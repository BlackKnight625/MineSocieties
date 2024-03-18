package ulisboa.tecnico.minesocieties.agents.location;

import com.google.gson.*;

import java.lang.reflect.Type;

public class LocationAccessJsonDeserializer implements JsonDeserializer<LocationAccess> {
    @Override
    public LocationAccess deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonPrimitive classNamePrimitive = jsonObject.get("type").getAsJsonPrimitive();

        String typeName = classNamePrimitive.getAsString();

        LocationAccessType type;

        try {
            type = LocationAccessType.valueOf(typeName);
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Invalid location access type: " + typeName);
        }

        return context.deserialize(json, type.getLocationAccessClass());
    }
}
