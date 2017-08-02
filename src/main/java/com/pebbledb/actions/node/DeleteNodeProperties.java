package com.pebbledb.actions.node;

import com.pebbledb.events.ExchangeEvent;
import com.pebbledb.server.Constants;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import static com.pebbledb.server.Server.graphs;

public class DeleteNodeProperties {

    public static void handle(ExchangeEvent exchangeEvent) {
        for (int i = -1; ++i < graphs.length; ) {
            graphs[i].deleteNodeProperties((String)exchangeEvent.getParameters().get(Constants.ID));
        }
        HttpServerExchange exchange = exchangeEvent.get();
        exchange.setStatusCode(StatusCodes.NO_CONTENT);
    }
}
