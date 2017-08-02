package com.pebbledb.actions;

import com.jsoniter.JsonIterator;
import com.jsoniter.spi.TypeLiteral;
import com.pebbledb.events.ExchangeEvent;

import java.util.Map;

import static com.pebbledb.server.Server.graphs;

public class PostNode {

    public static void handle(ExchangeEvent exchangeEvent) {
        String body = exchangeEvent.getBody();
        if (body.isEmpty()) {
            for (int i = -1; ++i < graphs.length; ) {
                graphs[i].addNode((String)exchangeEvent.getParameters().get("key"));
            }
        } else {
            Map<String, Object> properties = JsonIterator.deserialize(body, new TypeLiteral<Map<String, Object>>(){});

            for (int i = -1; ++i < graphs.length; ) {
                graphs[i].addNode((String)exchangeEvent.getParameters().get("key"), properties);
            }
        }
    }
}
