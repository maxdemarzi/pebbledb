package com.pebbledb.actions.related;

import com.pebbledb.events.ExchangeEvent;
import com.pebbledb.graphs.Direction;
import com.pebbledb.server.Constants;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import java.util.Map;

import static com.pebbledb.server.Server.graphs;

public class GetRelatedByType {
    static void handle(ExchangeEvent exchangeEvent, int number) {
        HttpServerExchange exchange = exchangeEvent.get();
        Map<String, String> parameters = exchangeEvent.getParameters();
        boolean exists;

        exists = graphs[number].related(parameters.get(Constants.LABEL1),
                parameters.get(Constants.FROM),
                parameters.get(Constants.LABEL2),
                parameters.get(Constants.TO), Direction.OUT, parameters.get(Constants.TYPE));

        if (exists) {
            exchange.setStatusCode(StatusCodes.OK);
        } else {
            exchange.setStatusCode(StatusCodes.NOT_FOUND);
        }
        exchangeEvent.clear();
    }
}
