package com.pebbledb.actions.nodeproperties;

import com.pebbledb.events.ExchangeEvent;
import com.pebbledb.server.Constants;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import static com.pebbledb.server.Server.graphs;

public class DeleteNodeProperties {

    public static void handle(ExchangeEvent exchangeEvent, int number, boolean respond) {
        boolean succeeded = graphs[number].deleteNodeProperties(exchangeEvent.getParameters().get(Constants.ID));

        if (respond) {
            HttpServerExchange exchange = exchangeEvent.get();
            if (succeeded) {
                exchange.setStatusCode(StatusCodes.NO_CONTENT);
            } else {
                exchange.setStatusCode(StatusCodes.NOT_FOUND);
            }
            exchangeEvent.clear();
        }
    }
}
