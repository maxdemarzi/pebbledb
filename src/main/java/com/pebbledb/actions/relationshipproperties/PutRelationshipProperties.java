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

public final class PutRelationshipProperties {
    private static final TypeLiteral<HashMap<String, Object>> MAP = new TypeLiteral<HashMap<String, Object>>(){};

    public static void handle(ExchangeEvent exchangeEvent, int number, boolean respond) {
        HttpServerExchange exchange = exchangeEvent.get();
        String body = exchangeEvent.getBody();
        Map<String, String> parameters = exchangeEvent.getParameters();
        boolean succeeded;
        Map<String, Object> relationship;

        if(parameters.containsKey(Constants.NUMBER)) {
            relationship = getRelationshipWithNumber(number, parameters);
            if(relationship == null) {
                if (respond) {
                    exchange.setStatusCode(StatusCodes.NOT_FOUND);
                    exchangeEvent.clear();
                }
                return;
            }

            if (body.isEmpty()) {
                succeeded = updateRelationshipPropertiesWithNumber(number, parameters, new HashMap<>());
            } else {
                    HashMap<String, Object> properties = JsonIterator.deserialize(body, new TypeLiteral<HashMap<String, Object>>(){});
                    succeeded = updateRelationshipPropertiesWithNumber(number, parameters, properties);
            }
            if (respond) {
                if (succeeded) {
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                    exchange.setStatusCode(StatusCodes.CREATED);
                    exchange.getResponseSender().send(
                            JsonStream.serialize(MAP, getRelationshipWithNumber(0, parameters)));
                } else {
                    exchange.setStatusCode(StatusCodes.NOT_MODIFIED);
                }
                exchangeEvent.clear();
            }

        } else {
            relationship = getRelationship(number, parameters);
            if(relationship == null) {
                if (respond) {
                    exchange.setStatusCode(StatusCodes.NOT_FOUND);
                }
                return;
            }

            if (body.isEmpty()) {
                succeeded = updateRelationshipProperties(number, parameters, new HashMap<>());
            } else {
                    HashMap<String, Object> properties = JsonIterator.deserialize(body, new TypeLiteral<HashMap<String, Object>>(){});
                succeeded = updateRelationshipProperties(number, parameters, properties);
            }
            if (respond) {
                if (succeeded) {
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                    exchange.setStatusCode(StatusCodes.CREATED);
                    exchange.getResponseSender().send(
                            JsonStream.serialize(MAP, getRelationship(number, parameters)));
                } else {
                    exchange.setStatusCode(StatusCodes.NOT_MODIFIED);
                }
                exchangeEvent.clear();
            }
        }

    }

    private static boolean updateRelationshipProperties(int number, Map<String, String> parameters, HashMap<String, Object> properties) {
        boolean succeeded;
        succeeded = graphs[number].updateRelationshipProperties(parameters.get(Constants.TYPE),
                parameters.get(Constants.FROM),
                parameters.get(Constants.TO),
                properties);
        return succeeded;
    }

    private static Map<String, Object> getRelationship(int number, Map<String, String> parameters) {
        Map<String, Object> relationship;
        relationship = graphs[number].getRelationship(
                parameters.get(Constants.TYPE),
                parameters.get(Constants.FROM),
                parameters.get(Constants.TO));
        return relationship;
    }

    private static boolean updateRelationshipPropertiesWithNumber(int number, Map<String, String> parameters, Map<String, Object> map) {
        boolean succeeded;
        succeeded = graphs[number].updateRelationshipProperties(parameters.get(Constants.TYPE),
                parameters.get(Constants.FROM),
                parameters.get(Constants.TO),
                Integer.parseInt(parameters.get(Constants.NUMBER)), map);
        return succeeded;
    }

    private static Map<String, Object> getRelationshipWithNumber(int number, Map<String, String> parameters) {
        Map<String, Object> relationship;
        relationship = graphs[number].getRelationship(
                parameters.get(Constants.TYPE),
                parameters.get(Constants.FROM),
                parameters.get(Constants.TO),
                Integer.parseInt(parameters.get(Constants.NUMBER)));
        return relationship;
    }
}
