package daoes;

import entities.Currency;
import io.vertx.core.Future;

import java.util.List;

public interface CurrencyDao {

    String GET_CURRENCY_LIST = "SELECT * FROM rates.currencies";

    Future<List<Currency>> getCurrencies();

}
