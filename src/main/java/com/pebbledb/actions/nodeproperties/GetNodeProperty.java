package com.pebbledb.actions.nodeproperties;

import com.jsoniter.output.JsonStream;
import com.pebbledb.events.ExchangeEvent;
import com.pebbledb.server.Constants;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

import static com.pebbledb.server.Server.graphs;

public interface GetNodeProperty {

    static void handle(ExchangeEvent exchangeEvent, int number) {
        HttpServerExchange exchange = exchangeEvent.get();

        Object property = graphs[number].getNodeProperty(
                exchangeEvent.getParameters().get(Constants.ID),
                exchangeEvent.getParameters().get(Constants.KEY));
        if (property == null) {
            exchange.setStatusCode(StatusCodes.NOT_FOUND);
        } else {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(
                JsonStream.serialize(property));
        }
        exchangeEvent.clear();
    }
}
