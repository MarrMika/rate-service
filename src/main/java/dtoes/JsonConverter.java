package dtoes;

import io.vertx.core.json.JsonObject;

import java.util.Map;

public interface JsonConverter {
    JsonObject toJson(Map<String, Object> values);
}
