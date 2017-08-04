package com.pebbledb.actions.relationship_type;

import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.TypeLiteral;
import com.pebbledb.events.ExchangeEvent;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.util.Map;

import static com.pebbledb.server.Server.graphs;

public class GetRelationshipTypesCount {
    public static void handle(ExchangeEvent exchangeEvent) {
        HttpServerExchange exchange = exchangeEvent.get();
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(
                JsonStream.serialize(new TypeLiteral<Map<String, Integer>>(){}, graphs[0].getRelationshipTypesCount()));
    }
}
