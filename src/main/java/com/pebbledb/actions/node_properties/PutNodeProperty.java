package com.pebbledb.actions.node_properties;

import com.jsoniter.JsonIterator;
import com.jsoniter.spi.TypeLiteral;
import com.pebbledb.events.ExchangeEvent;
import com.pebbledb.server.Constants;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import static com.pebbledb.server.Server.graphs;

public class PutNodeProperty {

    public static void handle(ExchangeEvent exchangeEvent, int number, boolean respond) {
        HttpServerExchange exchange = exchangeEvent.get();
        String body = exchangeEvent.getBody();
        boolean succeeded ;
        if (body.isEmpty()) {
            if(respond) {
                exchange.setStatusCode(StatusCodes.NOT_MODIFIED);
                exchangeEvent.clear();
            }
        } else {
            Object property = JsonIterator.deserialize(body, new TypeLiteral<Object>(){});
            succeeded = graphs[number].updateNodeProperty(exchangeEvent.getParameters().get(Constants.ID),
                    exchangeEvent.getParameters().get(Constants.KEY),
                    property);
            if (respond) {
                if (succeeded) {
                    exchange.setStatusCode(StatusCodes.NO_CONTENT);
                } else {
                    exchange.setStatusCode(StatusCodes.NOT_FOUND);
                }
                exchangeEvent.clear();
            }
        }
    }
}
