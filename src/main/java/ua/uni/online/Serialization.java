package ua.uni.online;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public final class Serialization {
    private static final Gson GSON = new Gson();
    private static final Type STRING_MAP_TYPE = new TypeToken<Map<String, String>>() { }.getType();

    private Serialization() {
    }

    public static String toJson(Map<String, String> data) {
        return GSON.toJson(data, STRING_MAP_TYPE);
    }

    public static Map<String, String> fromJson(String serialized) {
        return GSON.fromJson(serialized, STRING_MAP_TYPE);
    }

    public static String toJsonObject(Object value) {
        return GSON.toJson(value);
    }

    public static <T> T fromJson(String serialized, Class<T> type) {
        return GSON.fromJson(serialized, type);
    }

    public static String getStringField(String serialized, String fieldName) {
        JsonObject object = JsonParser.parseString(serialized).getAsJsonObject();
        if (!object.has(fieldName) || object.get(fieldName).isJsonNull()) {
            return null;
        }
        return object.get(fieldName).getAsString();
    }
}
