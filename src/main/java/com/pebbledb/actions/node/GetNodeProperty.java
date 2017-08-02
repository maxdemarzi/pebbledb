package com.pebbledb.actions.node;

import com.jsoniter.output.JsonStream;
import com.pebbledb.events.ExchangeEvent;
import com.pebbledb.server.Constants;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import static com.pebbledb.server.Server.graphs;

public class GetNodeProperty {
    public static void handle(ExchangeEvent exchangeEvent) {
        HttpServerExchange exchange = exchangeEvent.get();
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(
                JsonStream.serialize(graphs[0].getNodeProperty(
                        (String)exchangeEvent.getParameters().get(Constants.ID),
                        (String)exchangeEvent.getParameters().get(Constants.KEY))));
    }
}
