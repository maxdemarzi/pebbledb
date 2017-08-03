package com.pebbledb.actions.node;

import com.pebbledb.events.ExchangeEvent;
import com.pebbledb.server.Constants;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import static com.pebbledb.server.Server.graphs;

public class DeleteNodeProperty {
    public static void handle(ExchangeEvent exchangeEvent) {
        HttpServerExchange exchange = exchangeEvent.get();
        exchange.setStatusCode(StatusCodes.NO_CONTENT);
        for (int i = -1; ++i < graphs.length; ) {
            graphs[i].deleteNodeProperty((String)exchangeEvent.getParameters().get(Constants.ID),
                    (String)exchangeEvent.getParameters().get(Constants.KEY));
        }
    }
}
