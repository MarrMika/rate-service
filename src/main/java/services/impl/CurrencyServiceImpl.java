package services.impl;

import daoes.CurrencyDao;
import daoes.ExchangeRateDao;
import dtoes.ConvertDto;
import dtoes.ExchangeRateDto;
import dtoes.JsonConverter;
import entities.Currency;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.CurrencyService;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static java.util.Objects.isNull;

public class CurrencyServiceImpl implements CurrencyService {

    private CurrencyDao currencyDao;
    private ExchangeRateDao exchangeRateDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyServiceImpl.class);

    public CurrencyServiceImpl(CurrencyDao currencyDao, ExchangeRateDao exchangeRateDao) {
        this.currencyDao = currencyDao;
        this.exchangeRateDao = exchangeRateDao;
    }

    @Override
    public void handleGetSupportedSymbols(RoutingContext context) {
        currencyDao.getCurrencies().setHandler(
                resultHandler(context, result -> {
                    if (result == null) {
                        LOGGER.error("Service unavailable.");
                        serviceUnavailable(context);
                    } else {
                        final String encoded = Json.encodePrettily(Currency.toJsonList(result));
                        context.response()
                                .putHeader("content-type", "application/json")
                                .end(encoded);
                    }
                }));
    }

    @Override
    public void handleGetRates(RoutingContext context) {
        String base = context.request().getParam("base");
        List<String> symbols = context.request().params().getAll("symbols");

        if (base == null || base.isBlank() || symbols.get(0).isBlank()) {
            badRequest(context);
            return;
        } else {
            symbols.addAll(Set.of(symbols.get(0).split(",")));
            symbols.set(0, base);
        }

        String rateDate = context.request().getParam("date");
        ExchangeRateDto dto = new ExchangeRateDto();
        dto.setBase(base);
        dto.setSuccess(true);
        dto.setTimestamp(new Timestamp(System.currentTimeMillis()));
        if (isNull(rateDate)) {
            dto.setDate(LocalDate.now());
            dto.setHistorical(false);
        } else {
            dto.setDate(LocalDate.parse(rateDate));
        }

        getExchangeRateByRateDateAndIsoCode(context, symbols, dto.getDate(), dto);
    }

    @Override
    public void handleGetConvert(RoutingContext context) {
        String from = context.request().getParam("from");
        String to = context.request().getParam("to");
        Float amount = Float.valueOf(context.request().getParam("amount"));

        if (from == null || to == null || amount == null) {
            badRequest(context);
            return;
        }
        List<String> symbols = Arrays.asList(from, to);
        ConvertDto dto = new ConvertDto();
        dto.setFrom(from);
        dto.setTimestamp(new Timestamp(System.currentTimeMillis()));
        dto.setTo(to);
        dto.setAmount(amount);
        dto.setDate(LocalDate.now());

        exchangeRateDao.getExchangeRateByRateDateAndIsoCode(symbols, dto.getDate())
                .setHandler(
                        resultHandler(context, res -> {
                            if (res == null) {
                                LOGGER.error("Service unavailable.");
                                serviceUnavailable(context);
                            } else {
                                Float fromRate = res.get(symbols.get(0));
                                Float toRate = res.get(symbols.get(1));

                                if (fromRate == null || toRate == null) {
                                    notFound(context);
                                    return;
                                }
                                Float result = toRate * amount / fromRate;
                                context.response()
                                        .putHeader("content-type", "application/json")
                                        .end(Json.encodePrettily(
                                                dto.toJson(Map.of(
                                                        "rates", res,
                                                        "result", result)
                                                )
                                        ));
                            }
                        })
                );
    }

    private void getExchangeRateByRateDateAndIsoCode(
            RoutingContext context,
            List<String> symbols,
            LocalDate date,
            JsonConverter dto
    ) {
        exchangeRateDao.getExchangeRateByRateDateAndIsoCode(
                symbols,
                date
        )
                .setHandler(
                        resultHandler(context, result -> {
                            if (result == null) {
                                LOGGER.error("Service unavailable.");
                                serviceUnavailable(context);
                            } else {
                                final String baseIsoCode = symbols.get(0);
                                if (result.get(baseIsoCode) == null) {
                                    notFound(context);
                                    return;
                                }

                                final Float baseRate = Float.valueOf(result.get(baseIsoCode));
                                Map<String, Float> rates = result;
                                if (!baseIsoCode.equals("USD") && baseRate != 1f) {
                                    for (int i = 0; i < rates.size(); i++) {
                                        String key = symbols.get(i);
                                        Float value = rates.get(symbols.get(i));
                                        rates.replace(key, value, value / baseRate);
                                    }
                                }
                                context.response()
                                        .putHeader("content-type", "application/json")
                                        .end(Json.encodePrettily(
                                                dto.toJson(Map.of("rates", rates))
                                        ));
                            }
                        })
                );
    }

    private <T> Handler<AsyncResult<T>> resultHandler(RoutingContext context, Consumer<T> consumer) {
        return result -> {
            if (result.succeeded()) {
                consumer.accept(result.result());
            } else {
                LOGGER.error("Service unavailable. {}", result.cause().getMessage());
                serviceUnavailable(context);
            }
        };
    }


    private void badRequest(RoutingContext context) {
        context.response().setStatusCode(400).end();
    }

    private void notFound(RoutingContext context) {
        context.response().setStatusCode(404).end();
    }

    private void serviceUnavailable(RoutingContext context) {
        context.response().setStatusCode(503).end();
    }

}
