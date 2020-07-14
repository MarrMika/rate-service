package daoes.impl;

import daoes.CurrencyDao;
import entities.Currency;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;


public class CurrencyDaoImpl implements CurrencyDao {

    private final SQLClient client;
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyDaoImpl.class);

    public CurrencyDaoImpl(Vertx vertx, JsonObject config) {
        this.client = MySQLClient.createShared(vertx, config);
    }

    @Override
    public Future<List<Currency>> getCurrencies() {
        return Future.future(promise ->
                executeQuery(promise, GET_CURRENCY_LIST, r -> {
                    if (r.failed()) {
                        LOGGER.error("Get all fails. Cause: {}", r.cause().getMessage());
                        promise.fail(r.cause());
                    } else {
                        promise.complete(
                                r.result().getRows().stream().map(Currency::new).collect(Collectors.toList())
                        );
                    }
                })
        );
    }

    private void executeQuery(Promise failing, String sql, Handler<AsyncResult<ResultSet>> h) {
        client.getConnection(connHandler(failing, connection -> {
            connection.query(sql, ar -> {
                h.handle(ar);
                connection.close();
            });
        }));
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
}
