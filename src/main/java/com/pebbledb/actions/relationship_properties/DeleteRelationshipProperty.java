package com.pebbledb.actions.relationship_properties;

import com.pebbledb.events.ExchangeEvent;
import com.pebbledb.server.Constants;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import java.util.Map;

import static com.pebbledb.server.Server.graphs;

public class DeleteRelationshipProperty {

    public static void handle(ExchangeEvent exchangeEvent) {
        boolean succeeded = false;
        Map<String, String> parameters = exchangeEvent.getParameters();
        if(parameters.containsKey(Constants.NUMBER)) {
            for (int i = -1; ++i < graphs.length; ) {
                succeeded = graphs[i].deleteRelationshipProperty(
                        parameters.get(Constants.TYPE),
                        parameters.get(Constants.FROM),
                        parameters.get(Constants.TO),
                        Integer.parseInt(parameters.get(Constants.NUMBER)),
                        parameters.get(Constants.KEY));
            }
        } else {
            for (int i = -1; ++i < graphs.length; ) {
                succeeded = graphs[i].deleteRelationshipProperty(
                        parameters.get(Constants.TYPE),
                        parameters.get(Constants.FROM),
                        parameters.get(Constants.TO),
                        parameters.get(Constants.KEY));
            }
        }

        HttpServerExchange exchange = exchangeEvent.get();
        if (succeeded) {
            exchange.setStatusCode(StatusCodes.NO_CONTENT);
        } else {
            exchange.setStatusCode(StatusCodes.NOT_FOUND);
        }
    }
}
