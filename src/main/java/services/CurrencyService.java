package services;

import io.vertx.ext.web.RoutingContext;

public interface CurrencyService {

    void handleGetSupportedSymbols(RoutingContext context);

    void handleGetRates(RoutingContext context);

    void handleGetConvert(RoutingContext context);

}
