package com.pebbledb.actions.relationship_type;

import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.TypeLiteral;
import com.pebbledb.events.ExchangeEvent;
import com.pebbledb.server.Constants;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import static com.pebbledb.server.Server.graphs;

public class GetRelationshipTypeCount {
    public static void handle(ExchangeEvent exchangeEvent) {
        HttpServerExchange exchange = exchangeEvent.get();
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(
                JsonStream.serialize(
                        graphs[0].getRelationshipTypeCount(exchangeEvent.getParameters().get(Constants.TYPE))));
    }
}
