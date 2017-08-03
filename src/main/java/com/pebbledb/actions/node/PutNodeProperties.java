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

public class PutNodeProperties {

    public static void handle(ExchangeEvent exchangeEvent) {
        String body = exchangeEvent.getBody();
        String id = (String)exchangeEvent.getParameters().get(Constants.ID);
        boolean succeeded = false;
        if (body.isEmpty()) {
            for (int i = -1; ++i < graphs.length; ) {
                succeeded = graphs[i].updateNodeProperties(id, new HashMap());
            }
        } else {
            for (int i = -1; ++i < graphs.length; ) {
                HashMap<String, Object> properties = JsonIterator.deserialize(body, new TypeLiteral<HashMap<String, Object>>(){});
                succeeded = graphs[i].updateNodeProperties(id, properties);
            }
        }

        HttpServerExchange exchange = exchangeEvent.get();
        if (succeeded) {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.setStatusCode(StatusCodes.CREATED);
            exchange.getResponseSender().send(
                    JsonStream.serialize(new TypeLiteral<Map<String, Object>>(){}, graphs[0].getNode(id)));
        } else {
            exchange.setStatusCode(StatusCodes.NOT_MODIFIED);
        }
    }
}