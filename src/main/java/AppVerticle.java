import daoes.CurrencyDao;
import daoes.ExchangeRateDao;
import daoes.impl.CurrencyDaoImpl;
import daoes.impl.ExchangeRateDaoImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.CurrencyService;
import services.impl.CurrencyServiceImpl;
import utils.Constants;

public class AppVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppVerticle.class);

    @Override
    public void start(Future<Void> future) {

        long start = System.currentTimeMillis(); //seconds from app start counter
        Router router = Router.router(vertx); // router obj

        CurrencyDao currencyDao = new CurrencyDaoImpl(vertx, config());
        ExchangeRateDao exchangeRateDao = new ExchangeRateDaoImpl(vertx, config());
        CurrencyService currencyService = new CurrencyServiceImpl(currencyDao, exchangeRateDao);

        router.get(Constants.API_GET_SYMBOLS).handler(currencyService::handleGetSupportedSymbols);
        router.get(Constants.API_GET_LATEST_RATE).handler(currencyService::handleGetRates);
        router.get(Constants.API_GET_CONVERT).handler(currencyService::handleGetConvert);
        router.get(Constants.API_GET_HISTORICAL_RATE).handler(currencyService::handleGetRates);

        int httpPort = config().getInteger("http.port", Constants.HTTP_PORT);
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(httpPort,
                        result -> {
                            if (result.succeeded()) {
                                future.complete();
                                LOGGER.info("\nHttp server started in {} seconds. Little hurray!",
                                        (System.currentTimeMillis() - start) / 1000F);
                            } else {
                                future.fail(result.cause());
                                LOGGER.error("Creating http server error. Message: {}. Cause: {}",
                                        result.cause(), result.cause().getLocalizedMessage());
                            }
                        });

    }

}

