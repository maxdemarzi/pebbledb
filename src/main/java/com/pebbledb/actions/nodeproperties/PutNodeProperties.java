package com.pebbledb.actions.nodeproperties;

import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import com.pebbledb.events.ExchangeEvent;
import com.pebbledb.server.Constants;
import com.pebbledb.server.Types;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

import java.util.HashMap;

import static com.pebbledb.server.Server.graphs;

public interface PutNodeProperties {

    static void handle(ExchangeEvent exchangeEvent, int number, boolean respond) {
        HttpServerExchange exchange = exchangeEvent.get();
        String body = exchangeEvent.getBody();
        String id = exchangeEvent.getParameters().get(Constants.ID);
        String label = exchangeEvent.getParameters().get(Constants.LABEL);
        boolean succeeded;

        if (graphs[number].getNode(label, id) == null) {
            if (respond) {
                exchange.setStatusCode(StatusCodes.NOT_FOUND);
                exchangeEvent.clear();
            }
            return;
        }

        if (body.isEmpty()) {
             succeeded = graphs[number].updateNodeProperties(label, id, new HashMap());
        } else {
            HashMap<String, Object> properties = JsonIterator.deserialize(body, Types.MAP);
            succeeded = graphs[number].updateNodeProperties(label, id, properties);
        }
        if (respond) {
            if (succeeded) {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.setStatusCode(StatusCodes.CREATED);
                exchange.getResponseSender().send(
                        JsonStream.serialize(Types.MAP, graphs[number].getNode(label, id)));
            } else {
                exchange.setStatusCode(StatusCodes.NOT_MODIFIED);
            }
            exchangeEvent.clear();
        }
    }
}
