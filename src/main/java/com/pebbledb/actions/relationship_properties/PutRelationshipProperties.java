package com.pebbledb.actions.relationship_properties;

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

    public static void handle(ExchangeEvent exchangeEvent) {
        HttpServerExchange exchange = exchangeEvent.get();
        String body = exchangeEvent.getBody();
        Map<String, String> parameters = exchangeEvent.getParameters();
        boolean succeeded = false;
        Map<String, Object> relationship;

        if(parameters.containsKey(Constants.NUMBER)) {
            relationship = graphs[0].getRelationship(
                    parameters.get(Constants.TYPE),
                    parameters.get(Constants.FROM),
                    parameters.get(Constants.TO),
                    Integer.parseInt(parameters.get(Constants.NUMBER)));
            if(relationship == null) {
                exchange.setStatusCode(StatusCodes.NOT_FOUND);
                return;
            }

            if (body.isEmpty()) {
                for (int i = -1; ++i < graphs.length; ) {
                    succeeded = graphs[i].updateRelationshipProperties(parameters.get(Constants.TYPE),
                            parameters.get(Constants.FROM),
                            parameters.get(Constants.TO),
                            Integer.parseInt(parameters.get(Constants.NUMBER)), new HashMap<>());
                }
            } else {
                for (int i = -1; ++i < graphs.length; ) {
                    HashMap<String, Object> properties = JsonIterator.deserialize(body, new TypeLiteral<HashMap<String, Object>>(){});
                    succeeded = graphs[i].updateRelationshipProperties(parameters.get(Constants.TYPE),
                            parameters.get(Constants.FROM),
                            parameters.get(Constants.TO),
                            Integer.parseInt(parameters.get(Constants.NUMBER)), properties);
                }
            }
            if (succeeded) {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.setStatusCode(StatusCodes.CREATED);
                exchange.getResponseSender().send(
                        JsonStream.serialize(new TypeLiteral<Map<String, Object>>(){},
                                graphs[0].getRelationship(parameters.get(Constants.TYPE),
                                    parameters.get(Constants.FROM),
                                    parameters.get(Constants.TO),
                                    Integer.parseInt(parameters.get(Constants.NUMBER)))));
            } else {
                exchange.setStatusCode(StatusCodes.NOT_MODIFIED);
            }

        } else {
            relationship = graphs[0].getRelationship(
                    parameters.get(Constants.TYPE),
                    parameters.get(Constants.FROM),
                    parameters.get(Constants.TO));
            if(relationship == null) {
                exchange.setStatusCode(StatusCodes.NOT_FOUND);
                return;
            }

            if (body.isEmpty()) {
                for (int i = -1; ++i < graphs.length; ) {
                    succeeded = graphs[i].updateRelationshipProperties(parameters.get(Constants.TYPE),
                            parameters.get(Constants.FROM),
                            parameters.get(Constants.TO),
                            new HashMap<>());
                }
            } else {
                for (int i = -1; ++i < graphs.length; ) {
                    HashMap<String, Object> properties = JsonIterator.deserialize(body, new TypeLiteral<HashMap<String, Object>>(){});
                    succeeded = graphs[i].updateRelationshipProperties(parameters.get(Constants.TYPE),
                            parameters.get(Constants.FROM),
                            parameters.get(Constants.TO),
                            properties);
                }
            }
            if (succeeded) {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.setStatusCode(StatusCodes.CREATED);
                exchange.getResponseSender().send(JsonStream.serialize(new TypeLiteral<Map<String, Object>>(){},
                        graphs[0].getRelationship(parameters.get(Constants.TYPE),
                                parameters.get(Constants.FROM),
                                parameters.get(Constants.TO))));
            } else {
                exchange.setStatusCode(StatusCodes.NOT_MODIFIED);
            }
        }



    }
}
