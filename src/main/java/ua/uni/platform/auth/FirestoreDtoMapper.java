package ua.uni.platform.auth;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Map;

final class FirestoreDtoMapper {
    private static final Gson GSON = new Gson();

    private FirestoreDtoMapper() {
    }

    static JsonObject toDocumentBody(Object dto) {
        JsonObject body = new JsonObject();
        body.add("fields", toFields(dto));
        return body;
    }

    static JsonObject toFields(Object dto) {
        JsonElement tree = GSON.toJsonTree(dto);
        JsonObject source = tree != null && tree.isJsonObject() ? tree.getAsJsonObject() : new JsonObject();
        return toFirestoreFields(source);
    }

    static <T> T fromDocument(JsonObject document, Class<T> type) {
        if (document == null || !document.has("fields") || !document.get("fields").isJsonObject()) {
            return null;
        }
        JsonObject plainObject = fromFirestoreFields(document.getAsJsonObject("fields"));
        return GSON.fromJson(plainObject, type);
    }

    private static JsonObject toFirestoreFields(JsonObject source) {
        JsonObject fields = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : source.entrySet()) {
            fields.add(entry.getKey(), toFirestoreValue(entry.getValue()));
        }
        return fields;
    }

    private static JsonObject toFirestoreValue(JsonElement value) {
        JsonObject firestoreValue = new JsonObject();
        if (value == null || value.isJsonNull()) {
            firestoreValue.addProperty("nullValue", "NULL_VALUE");
            return firestoreValue;
        }
        if (value.isJsonPrimitive()) {
            JsonPrimitive primitive = value.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                firestoreValue.addProperty("booleanValue", primitive.getAsBoolean());
                return firestoreValue;
            }
            if (primitive.isNumber()) {
                Number number = primitive.getAsNumber();
                String raw = primitive.getAsString();
                if (raw.contains(".") || raw.contains("e") || raw.contains("E")) {
                    firestoreValue.addProperty("doubleValue", number.doubleValue());
                } else {
                    firestoreValue.addProperty("integerValue", raw);
                }
                return firestoreValue;
            }
            firestoreValue.addProperty("stringValue", primitive.getAsString());
            return firestoreValue;
        }
        if (value.isJsonArray()) {
            JsonArray encodedValues = new JsonArray();
            for (JsonElement element : value.getAsJsonArray()) {
                encodedValues.add(toFirestoreValue(element));
            }
            JsonObject arrayValue = new JsonObject();
            arrayValue.add("values", encodedValues);
            firestoreValue.add("arrayValue", arrayValue);
            return firestoreValue;
        }

        JsonObject mapValue = new JsonObject();
        mapValue.add("fields", toFirestoreFields(value.getAsJsonObject()));
        firestoreValue.add("mapValue", mapValue);
        return firestoreValue;
    }

    private static JsonObject fromFirestoreFields(JsonObject fields) {
        JsonObject plain = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : fields.entrySet()) {
            JsonElement val = entry.getValue();
            if (val == null || !val.isJsonObject()) {
                plain.add(entry.getKey(), JsonNull.INSTANCE);
                continue;
            }
            plain.add(entry.getKey(), fromFirestoreValue(val.getAsJsonObject()));
        }
        return plain;
    }

    private static JsonElement fromFirestoreValue(JsonObject firestoreValue) {
        if (firestoreValue.has("nullValue")) {
            return JsonNull.INSTANCE;
        }
        if (firestoreValue.has("stringValue")) {
            return new JsonPrimitive(firestoreValue.get("stringValue").getAsString());
        }
        if (firestoreValue.has("booleanValue")) {
            return new JsonPrimitive(firestoreValue.get("booleanValue").getAsBoolean());
        }
        if (firestoreValue.has("integerValue")) {
            return new JsonPrimitive(Long.parseLong(firestoreValue.get("integerValue").getAsString()));
        }
        if (firestoreValue.has("doubleValue")) {
            return new JsonPrimitive(firestoreValue.get("doubleValue").getAsDouble());
        }
        if (firestoreValue.has("arrayValue")) {
            JsonArray decodedValues = new JsonArray();
            JsonObject arrayValue = firestoreValue.getAsJsonObject("arrayValue");
            if (arrayValue != null && arrayValue.has("values")) {
                for (JsonElement element : arrayValue.getAsJsonArray("values")) {
                    decodedValues.add(fromFirestoreValue(element.getAsJsonObject()));
                }
            }
            return decodedValues;
        }
        if (firestoreValue.has("mapValue")) {
            JsonObject mapValue = firestoreValue.getAsJsonObject("mapValue");
            if (mapValue != null && mapValue.has("fields")) {
                return fromFirestoreFields(mapValue.getAsJsonObject("fields"));
            }
            return new JsonObject();
        }
        return JsonNull.INSTANCE;
    }
}
