package com.pebbledb.actions.node;

import com.jsoniter.JsonIterator;
import com.jsoniter.spi.TypeLiteral;
import com.pebbledb.events.ExchangeEvent;
import com.pebbledb.server.Constants;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import static com.pebbledb.server.Server.graphs;

public class PutNodeProperty {

    public static void handle(ExchangeEvent exchangeEvent) {
        HttpServerExchange exchange = exchangeEvent.get();
        String body = exchangeEvent.getBody();
        if (body.isEmpty()) {
            exchange.setStatusCode(StatusCodes.NOT_MODIFIED);
        } else {
            Object property = JsonIterator.deserialize(body, new TypeLiteral<Object>(){});

            for (int i = -1; ++i < graphs.length; ) {
                graphs[i].updateNodeProperty((String)exchangeEvent.getParameters().get(Constants.ID),
                        (String)exchangeEvent.getParameters().get(Constants.KEY),
                        property);
            }
            exchange.setStatusCode(StatusCodes.NO_CONTENT);
        }
    }
}
