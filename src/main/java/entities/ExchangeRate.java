package entities;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ExchangeRate {
    private String id;
    private String currencyId;
    private Float exchangeRate;
    private LocalDate rateDate;

    public ExchangeRate(JsonObject json) {
        fromJson(json);
    }

    private void fromJson(JsonObject json) {
        id = json.getString("id");
        currencyId = json.getString("currencyId");
        exchangeRate = json.getFloat("exchangeRate");
        rateDate = LocalDate.parse(
                json.getString("rateDate")
        );
    }
}
