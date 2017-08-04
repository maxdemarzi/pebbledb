package com.pebbledb.actions.relationship_properties;

import com.jsoniter.JsonIterator;
import com.jsoniter.spi.TypeLiteral;
import com.pebbledb.events.ExchangeEvent;
import com.pebbledb.server.Constants;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import java.util.Map;

import static com.pebbledb.server.Server.graphs;

public class PutRelationshipProperty {

    public static void handle(ExchangeEvent exchangeEvent, int number, boolean respond) {
        HttpServerExchange exchange = exchangeEvent.get();
        String body = exchangeEvent.getBody();

        if (body.isEmpty()) {
            if (respond) {
                exchange.setStatusCode(StatusCodes.NOT_MODIFIED);
                exchangeEvent.clear();
            }
        } else {
            boolean succeeded = false;
            Map<String, String> parameters = exchangeEvent.getParameters();

            if (parameters.containsKey(Constants.NUMBER)) {
                    Object property = JsonIterator.deserialize(body, new TypeLiteral<Object>() {});

                    succeeded = graphs[number].updateRelationshipProperty(
                            parameters.get(Constants.TYPE),
                            parameters.get(Constants.FROM),
                            parameters.get(Constants.TO),
                            Integer.parseInt(parameters.get(Constants.NUMBER)),
                            parameters.get(Constants.KEY),
                            property);
            } else {
                    Object property = JsonIterator.deserialize(body, new TypeLiteral<Object>() {});

                    succeeded = graphs[number].updateRelationshipProperty(
                            parameters.get(Constants.TYPE),
                            parameters.get(Constants.FROM),
                            parameters.get(Constants.TO),
                            parameters.get(Constants.KEY),
                            property);
            }
            if (respond) {
                if (succeeded) {
                    exchange.setStatusCode(StatusCodes.NO_CONTENT);
                } else {
                    exchange.setStatusCode(StatusCodes.NOT_FOUND);
                }
                exchangeEvent.clear();
            }
        }
    }
}
