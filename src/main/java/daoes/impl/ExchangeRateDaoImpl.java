package daoes.impl;

import daoes.ExchangeRateDao;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeRateDaoImpl implements ExchangeRateDao {

    private final SQLClient client;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeRateDaoImpl.class);

    public ExchangeRateDaoImpl(Vertx vertx, JsonObject config) {
        this.client = MySQLClient.createShared(vertx, config);
    }


    @Override
    public Future<Map<String, Float>> getExchangeRateByRateDateAndIsoCode(List<String> isoCodes, LocalDate rateDate) {
        return Future.future(promise -> {
                    String query = String.format(
                            GET_CURRENCY_BY_RATE_DATE,
                            String.format(
                                    "\'%s\'",
                                    String.join("\',\'", isoCodes)
                            )
                    );

                    executePreparedQuery(
                            promise,
                            query,
                            new JsonArray().add(String.valueOf(rateDate)),
                            r -> {
                                if (r.failed()) {
                                    LOGGER.error("Get categories by isoCode and date fails. Cause: {}", r.cause().getMessage());
                                    promise.fail(r.cause());
                                } else {
                                    LOGGER.debug("Data were successfully retrieved! Result: [{}]", r.result());
                                    promise.complete(getMultiple(r.result()));
                                }
                            });
                }
        );
    }


    private void executePreparedQuery(Promise failing, String sql, JsonArray params, Handler<AsyncResult<ResultSet>> h) {
        client.getConnection(connHandler(
                failing,
                connection -> {
                    connection.queryWithParams(sql, params, ar -> {
                        h.handle(ar);
                        connection.close();
                    });
                })
        );
    }

    private Handler<AsyncResult<SQLConnection>> connHandler(Promise future, Handler<SQLConnection> handler) {
        return conn -> {
            if (conn.succeeded()) {
                final SQLConnection connection = conn.result();
                handler.handle(connection);
            } else {
                LOGGER.error("Connection failed. Cause: {}", conn.cause().getMessage());
                future.fail(conn.cause());
            }
        };
    }

    private Map<String, Float> getMultiple(ResultSet from) {
        Map<String, Float> map = new HashMap<>();
        List<JsonObject> list = from.getRows();
        list.stream().forEach(jsonObject -> {
            String isoCode = jsonObject.getString("isoCode");
            Float exchangeRate = jsonObject.getFloat("exchangeRate");
            map.put(isoCode, exchangeRate);
        });

        return map;
    }
}
