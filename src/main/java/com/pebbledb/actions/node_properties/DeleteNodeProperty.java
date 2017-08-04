package com.pebbledb.actions.node_properties;

import com.pebbledb.events.ExchangeEvent;
import com.pebbledb.server.Constants;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import static com.pebbledb.server.Server.graphs;

public class DeleteNodeProperty {
    public static void handle(ExchangeEvent exchangeEvent) {
        boolean succeeded = false;
        for (int i = -1; ++i < graphs.length; ) {
            succeeded = graphs[i].deleteNodeProperty(exchangeEvent.getParameters().get(Constants.ID),
                    exchangeEvent.getParameters().get(Constants.KEY));
        }
        HttpServerExchange exchange = exchangeEvent.get();
        if (succeeded) {
            exchange.setStatusCode(StatusCodes.NO_CONTENT);
        } else {
            exchange.setStatusCode(StatusCodes.NOT_FOUND);
        }
    }
}
