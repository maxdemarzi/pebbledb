package com.pebbledb.actions.relationshipproperties;

import com.pebbledb.events.ExchangeEvent;
import com.pebbledb.server.Constants;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import java.util.Map;

import static com.pebbledb.server.PebbleServer.graphs;

public interface DeleteRelationshipProperty {

    static void handle(ExchangeEvent exchangeEvent, int number, boolean respond) {
        boolean succeeded;
        Map<String, String> parameters = exchangeEvent.getParameters();
        if(parameters.containsKey(Constants.NUMBER)) {
                succeeded = graphs[number].deleteRelationshipProperty(
                        parameters.get(Constants.TYPE),
                        exchangeEvent.getParameters().get(Constants.LABEL1),
                        parameters.get(Constants.FROM),
                        exchangeEvent.getParameters().get(Constants.LABEL2),
                        parameters.get(Constants.TO),
                        Integer.parseInt(parameters.get(Constants.NUMBER)),
                        parameters.get(Constants.KEY));
        } else {
                succeeded = graphs[number].deleteRelationshipProperty(
                        parameters.get(Constants.TYPE),
                        exchangeEvent.getParameters().get(Constants.LABEL1),
                        parameters.get(Constants.FROM),
                        exchangeEvent.getParameters().get(Constants.LABEL2),
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
