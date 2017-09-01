package com.pebbledb.actions.relationshiptype;

import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.TypeLiteral;
import com.pebbledb.events.ExchangeEvent;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.util.Map;

import static com.pebbledb.server.Server.graphs;

public interface GetRelationshipTypesCount {

    static void handle(ExchangeEvent exchangeEvent, int number) {
        HttpServerExchange exchange = exchangeEvent.get();
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(
                JsonStream.serialize(new TypeLiteral<Map<String, Integer>>(){}, graphs[number].getRelationshipTypesCount()));
        exchangeEvent.clear();
    }
}
