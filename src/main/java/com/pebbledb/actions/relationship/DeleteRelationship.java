package com.pebbledb.actions.relationship;

import com.pebbledb.events.ExchangeEvent;
import com.pebbledb.server.Constants;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

import java.util.Map;

import static com.pebbledb.server.Server.graphs;

public class DeleteRelationship {

    public static void handle(ExchangeEvent exchangeEvent, int number, boolean respond) {
        HttpServerExchange exchange = exchangeEvent.get();
        Map<String, String> parameters = exchangeEvent.getParameters();

        boolean succeeded;

        if(parameters.containsKey(Constants.NUMBER)) {
                succeeded = graphs[number].removeRelationship(parameters.get(Constants.TYPE),
                        parameters.get(Constants.FROM),
                        parameters.get(Constants.TO),
                        Integer.parseInt(parameters.get(Constants.NUMBER)));

        } else {
                succeeded = graphs[number].removeRelationship(parameters.get(Constants.TYPE),
                        parameters.get(Constants.FROM),
                        parameters.get(Constants.TO));

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
