package com.pebbledb.actions.relationshiptype;

import com.jsoniter.output.JsonStream;
import com.pebbledb.events.ExchangeEvent;
import com.pebbledb.server.Types;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import static com.pebbledb.server.PebbleServer.graphs;

public interface GetRelationshipTypes {

    static void handle(ExchangeEvent exchangeEvent, int number) {
        HttpServerExchange exchange = exchangeEvent.get();
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(
                JsonStream.serialize(Types.SET, graphs[number].getRelationshipTypes()));
        exchangeEvent.clear();
    }
}
