package com.pebbledb.actions.relationshipproperties;

import com.pebbledb.events.ExchangeEvent;
import com.pebbledb.server.Constants;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import java.util.Map;

import static com.pebbledb.server.Server.graphs;

public class DeleteRelationshipProperty {

    public static void handle(ExchangeEvent exchangeEvent, int number, boolean respond) {
        boolean succeeded;
        Map<String, String> parameters = exchangeEvent.getParameters();
        if(parameters.containsKey(Constants.NUMBER)) {
                succeeded = graphs[number].deleteRelationshipProperty(
                        parameters.get(Constants.TYPE),
                        parameters.get(Constants.FROM),
                        parameters.get(Constants.TO),
                        Integer.parseInt(parameters.get(Constants.NUMBER)),
                        parameters.get(Constants.KEY));
        } else {
                succeeded = graphs[number].deleteRelationshipProperty(
                        parameters.get(Constants.TYPE),
                        parameters.get(Constants.FROM),
                        parameters.get(Constants.TO),
                        parameters.get(Constants.KEY));
        }

        if (respond) {
            HttpServerExchange exchange = exchangeEvent.get();
            if (succeeded) {
                exchange.setStatusCode(StatusCodes.NO_CONTENT);
            } else {
                exchange.setStatusCode(StatusCodes.NOT_FOUND);
            }
            exchangeEvent.clear();
        }
    }
}
