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

    public static void handle(ExchangeEvent exchangeEvent) {
        HttpServerExchange exchange = exchangeEvent.get();
        String body = exchangeEvent.getBody();

        if (body.isEmpty()) {
            exchange.setStatusCode(StatusCodes.NOT_MODIFIED);
        } else {
            boolean succeeded = false;
            Map<String, String> parameters = exchangeEvent.getParameters();

            if (parameters.containsKey(Constants.NUMBER)) {
                for (int i = -1; ++i < graphs.length; ) {
                    Object property = JsonIterator.deserialize(body, new TypeLiteral<Object>() {
                    });

                    succeeded = graphs[i].updateRelationshipProperty(
                            parameters.get(Constants.TYPE),
                            parameters.get(Constants.FROM),
                            parameters.get(Constants.TO),
                            Integer.parseInt(parameters.get(Constants.NUMBER)),
                            parameters.get(Constants.KEY),
                            property);
                }
            } else {
                for (int i = -1; ++i < graphs.length; ) {
                    Object property = JsonIterator.deserialize(body, new TypeLiteral<Object>() {
                    });

                    succeeded = graphs[i].updateRelationshipProperty(
                            parameters.get(Constants.TYPE),
                            parameters.get(Constants.FROM),
                            parameters.get(Constants.TO),
                            parameters.get(Constants.KEY),
                            property);
                }
            }

            if (succeeded) {
                exchange.setStatusCode(StatusCodes.NO_CONTENT);
            } else {
                exchange.setStatusCode(StatusCodes.NOT_FOUND);
            }

        }
    }
}
