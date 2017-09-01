package com.pebbledb.actions.node;

import com.jsoniter.JsonIterator;
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

public interface PostNode {

    static void handle(ExchangeEvent exchangeEvent, int number, boolean respond) {
        String body = exchangeEvent.getBody();
        String id = exchangeEvent.getParameters().get(Constants.ID);
        if (body.isEmpty()) {
                graphs[number].addNode(id);
        } else {
            HashMap<String, Object> properties = JsonIterator.deserialize(body, new TypeLiteral<HashMap<String, Object>>(){});
            graphs[number].addNode(id, properties);
        }
        if (respond) {
            HttpServerExchange exchange = exchangeEvent.get();
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.setStatusCode(StatusCodes.CREATED);
            exchange.getResponseSender().send(
                    JsonStream.serialize(new TypeLiteral<Map<String, Object>>() {
                    }, graphs[number].getNode(id)));
            exchangeEvent.clear();
        }
    }
}
