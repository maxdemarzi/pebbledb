package com.pebbledb.server;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.TypeLiteral;
import com.pebbledb.events.ExchangeEvent;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.util.Map;

import static com.pebbledb.server.Server.graphs;

public class NodeHandler {

    /*
            /db/node/:id
            /db/node/:id/degree/:direction
            /db/node/:id/property/:key
            /db/node/:id/properties
     */

    public static void handle(ExchangeEvent exchangeEvent) {
        HttpServerExchange exchange = exchangeEvent.get();
        String[] pathArray = exchangeEvent.getPath().split("/");
        if (pathArray.length == 4) {
            if (exchangeEvent.getWrite()) {
                String body = exchangeEvent.getBody();
                if (body.isEmpty()) {
                    for (int i = -1; ++i < graphs.length; ) {
                        graphs[i].addNode(pathArray[3]);
                    }
                } else {
                    Map<String, Object> properties = JsonIterator.deserialize(body, new TypeLiteral<Map<String, Object>>(){});

                    for (int i = -1; ++i < graphs.length; ) {
                        graphs[i].addNode(pathArray[3], properties);
                    }
                }
            } else {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(JsonStream.serialize(graphs[0].getNode(pathArray[3])));
            }
        }
    }
}
