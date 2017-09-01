package com.pebbledb.actions.relationshipproperties;

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

public class PutRelationshipProperties {

    public static void handle(ExchangeEvent exchangeEvent, int number, boolean respond) {
        HttpServerExchange exchange = exchangeEvent.get();
        String body = exchangeEvent.getBody();
        Map<String, String> parameters = exchangeEvent.getParameters();
        boolean succeeded = false;
        Map<String, Object> relationship;

        if(parameters.containsKey(Constants.NUMBER)) {
            relationship = graphs[number].getRelationship(
                    parameters.get(Constants.TYPE),
                    parameters.get(Constants.FROM),
                    parameters.get(Constants.TO),
                    Integer.parseInt(parameters.get(Constants.NUMBER)));
            if(relationship == null) {
                if (respond) {
                    exchange.setStatusCode(StatusCodes.NOT_FOUND);
                    exchangeEvent.clear();
                }
                return;
            }

            if (body.isEmpty()) {
                    succeeded = graphs[number].updateRelationshipProperties(parameters.get(Constants.TYPE),
                            parameters.get(Constants.FROM),
                            parameters.get(Constants.TO),
                            Integer.parseInt(parameters.get(Constants.NUMBER)), new HashMap<>());
            } else {
                    HashMap<String, Object> properties = JsonIterator.deserialize(body, new TypeLiteral<HashMap<String, Object>>(){});
                    succeeded = graphs[number].updateRelationshipProperties(parameters.get(Constants.TYPE),
                            parameters.get(Constants.FROM),
                            parameters.get(Constants.TO),
                            Integer.parseInt(parameters.get(Constants.NUMBER)), properties);
            }
            if (respond) {
                if (succeeded) {
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                    exchange.setStatusCode(StatusCodes.CREATED);
                    exchange.getResponseSender().send(
                            JsonStream.serialize(new TypeLiteral<Map<String, Object>>() {
                                                 },
                                    graphs[0].getRelationship(parameters.get(Constants.TYPE),
                                            parameters.get(Constants.FROM),
                                            parameters.get(Constants.TO),
                                            Integer.parseInt(parameters.get(Constants.NUMBER)))));
                } else {
                    exchange.setStatusCode(StatusCodes.NOT_MODIFIED);
                }
                exchangeEvent.clear();
            }

        } else {
            relationship = graphs[number].getRelationship(
                    parameters.get(Constants.TYPE),
                    parameters.get(Constants.FROM),
                    parameters.get(Constants.TO));
            if(relationship == null) {
                if (respond) {
                    exchange.setStatusCode(StatusCodes.NOT_FOUND);
                }
                return;
            }

            if (body.isEmpty()) {
                    succeeded = graphs[number].updateRelationshipProperties(parameters.get(Constants.TYPE),
                            parameters.get(Constants.FROM),
                            parameters.get(Constants.TO),
                            new HashMap<>());
            } else {
                    HashMap<String, Object> properties = JsonIterator.deserialize(body, new TypeLiteral<HashMap<String, Object>>(){});
                    succeeded = graphs[number].updateRelationshipProperties(parameters.get(Constants.TYPE),
                            parameters.get(Constants.FROM),
                            parameters.get(Constants.TO),
                            properties);
            }
            if (respond) {
                if (succeeded) {
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                    exchange.setStatusCode(StatusCodes.CREATED);
                    exchange.getResponseSender().send(JsonStream.serialize(new TypeLiteral<Map<String, Object>>() {
                                                                           },
                            graphs[0].getRelationship(parameters.get(Constants.TYPE),
                                    parameters.get(Constants.FROM),
                                    parameters.get(Constants.TO))));
                } else {
                    exchange.setStatusCode(StatusCodes.NOT_MODIFIED);
                }
                exchangeEvent.clear();
            }
        }

    }
}
