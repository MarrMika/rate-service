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
public class ExchangeRateDto implements JsonConverter {
    private Boolean success;
    private Timestamp timestamp;
    private Boolean historical = true;
    private String base;
    private LocalDate date;
    private Map<String, Float> rates;


    @Override
    public JsonObject toJson(Map<String, Object> values) {
        JsonObject json = new JsonObject();
        json.put("success", success);
        json.put("timestamp", timestamp.toString());
        json.put("historical", historical);
        json.put("base", base);
        json.put("date", date.toString());
        json.put("rates", values.get("rates"));
        return json;
    }
}
