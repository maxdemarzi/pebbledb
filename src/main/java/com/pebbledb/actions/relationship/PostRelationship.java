package com.pebbledb.actions.relationship;

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

public interface PostRelationship {

    static void handle(ExchangeEvent exchangeEvent, int number, boolean respond) {
        HttpServerExchange exchange = exchangeEvent.get();
        Map<String, String> parameters = exchangeEvent.getParameters();
        String body = exchangeEvent.getBody();

        if (body.isEmpty()) {

            graphs[number].addRelationship(parameters.get(Constants.TYPE),
                    parameters.get(Constants.FROM),
                    parameters.get(Constants.TO));

            if (respond) {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.setStatusCode(StatusCodes.CREATED);
                exchange.getResponseSender().send(
                        JsonStream.serialize(new TypeLiteral<Map<String, Object>>() {
                                             },
                                graphs[number].getRelationship(parameters.get(Constants.TYPE),
                                        parameters.get(Constants.FROM),
                                        parameters.get(Constants.TO))));
                exchangeEvent.clear();
            }
        } else {

                HashMap<String, Object> properties = JsonIterator.deserialize(body, new TypeLiteral<HashMap<String, Object>>(){});
                graphs[number].addRelationship(parameters.get(Constants.TYPE),
                        parameters.get(Constants.FROM),
                        parameters.get(Constants.TO), properties);

            if (respond) {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.setStatusCode(StatusCodes.CREATED);
                exchange.getResponseSender().send(
                        JsonStream.serialize(new TypeLiteral<Map<String, Object>>() {},
                                graphs[number].getRelationship(parameters.get(Constants.TYPE),
                                        parameters.get(Constants.FROM),
                                        parameters.get(Constants.TO))));
                exchangeEvent.clear();
            }
        }

    }
}
