package dtoes;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConvertDto implements JsonConverter {
    private Boolean success;
    private String from;
    private String to;
    private Float amount;
    private Timestamp timestamp;
    private LocalDate date;

    public JsonObject toJson(Map<String, Object> values) {
        JsonObject json = new JsonObject();
        json.put("success", true);
        json.put("query",
                new JsonObject()
                        .put("from", from)
                        .put("to", to)
                        .put("amount", amount)
        );
        json.put("info",
                new JsonObject()
                        .put("rate", ((Map<String, Float>) values.get("rates")).get(to))
                        .put("timestamp", timestamp.toString())
        );
        json.put("date", date.toString());
        json.put("result", values.get("result"));
        return json;
    }
}
