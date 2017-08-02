package com.pebbledb.server;

import com.pebbledb.actions.Action;
import com.pebbledb.events.ExchangeEvent;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.PathTemplateMatch;

import java.util.HashMap;
import java.util.Map;

public class GetNodePropertyHandler implements HttpHandler {

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {

        final long seq = Server.ringBuffer.next();
        final ExchangeEvent exchangeEvent = Server.ringBuffer.get(seq);

        Map<String, String> parameters = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY ).getParameters();

        exchangeEvent.setRequest(false, Action.GET_NODE_PROPERTY,
                new HashMap<String, Object>() {{
                    put(Constants.ID, parameters.get( Constants.ID ));
                    put(Constants.KEY, parameters.get( Constants.KEY ));
                }});

        exchangeEvent.set(exchange);
        Server.ringBuffer.publish(seq);
        // This is deprecated but it works...
        exchange.dispatch();

    }
}