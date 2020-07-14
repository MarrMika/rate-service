package daoes;

import io.vertx.core.Future;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ExchangeRateDao {

    String GET_CURRENCY_BY_RATE_DATE =
            "SELECT distinct c.isoCode, r.exchangeRate " +
                    "FROM rates.exchangeRates as r, " +
                    "rates.currencies as c " +
                    "WHERE r.currencyId = c.id " +
                    "AND c.isoCode IN (%s) " +
                    "AND r.rateDate = ?";

    Future<Map<String, Float>> getExchangeRateByRateDateAndIsoCode(List<String> isoCodes, LocalDate rateDate);

}
