package entities;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class Currency {
    private String id;
    private String isoCode;
    private String name;

    public Currency(JsonObject json) {
        fromJson(json);
    }

    private void fromJson(JsonObject json) {
        id = json.getString("id");
        isoCode = json.getString("isoCode");
        name = json.getString("name");
    }

    public static List<JsonObject> toJsonList(List<Currency> currencies) {
        JsonObject json = new JsonObject();
        Map<String, String> symbols = new HashMap<>();
        currencies.stream().forEach(currency -> symbols.put(currency.isoCode, currency.name));
        json.put("success", true);
        json.put("symbols", symbols);

        return Arrays.asList(json);
    }
}
