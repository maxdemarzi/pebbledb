package com.pebbledb.actions.relationship;

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

public interface GetRelationship {

    static void handle(ExchangeEvent exchangeEvent, int number) {
        HttpServerExchange exchange = exchangeEvent.get();
        Map<String, String> parameters = exchangeEvent.getParameters();
        Map<String, Object> relationship;

        if(parameters.containsKey(Constants.NUMBER)) {
            relationship = graphs[number].getRelationship(
                    parameters.get(Constants.TYPE),
                    parameters.get(Constants.FROM),
                    parameters.get(Constants.TO),
                    Integer.parseInt(parameters.get(Constants.NUMBER)));
        } else {
            relationship = graphs[number].getRelationship(
                    parameters.get(Constants.TYPE),
                    parameters.get(Constants.FROM),
                    parameters.get(Constants.TO));
        }

        if (relationship == null) {
            exchange.setStatusCode(StatusCodes.NOT_FOUND);
        } else {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(
                    JsonStream.serialize(new TypeLiteral<HashMap<String, Object>>(){}, relationship));
        }
        exchangeEvent.clear();
    }

}
