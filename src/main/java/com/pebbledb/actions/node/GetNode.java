package com.pebbledb.actions.node;

import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.TypeLiteral;
import com.pebbledb.events.ExchangeEvent;
import com.pebbledb.server.Constants;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

import java.util.HashMap;
import java.util.Map;

import static com.pebbledb.server.Server.graphs;

public final class GetNode {

    private static final TypeLiteral<HashMap<String, Object>> MAP = new TypeLiteral<HashMap<String, Object>>(){};

    public static void handle(ExchangeEvent exchangeEvent, int number) {
        HttpServerExchange exchange = exchangeEvent.get();

        Map<String, Object> node = graphs[number].getNode(exchangeEvent.getParameters().get(Constants.ID));
        if (node == null) {
            exchange.setStatusCode(StatusCodes.NOT_FOUND);
        } else {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(
                    JsonStream.serialize(MAP, node));
        }
        exchangeEvent.clear();
    }

}
