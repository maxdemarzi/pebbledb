package com.pebbledb.server;

import com.pebbledb.actions.Action;
import com.pebbledb.events.ExchangeEvent;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.PathTemplateMatch;

import java.util.HashMap;

public class PostNodeHandler implements HttpHandler {

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {

        final long seq = Server.ringBuffer.next();
        final ExchangeEvent exchangeEvent = Server.ringBuffer.get(seq);

        String key = exchange.getAttachment( PathTemplateMatch.ATTACHMENT_KEY )
                .getParameters().get( "key" );

        exchangeEvent.setRequest(true, Action.POST_NODE, new HashMap<String, Object>() {{put("key", key);}});

        exchangeEvent.set(exchange);
        Server.ringBuffer.publish(seq);
        // This is deprecated but it works...
        exchange.dispatch();

    }
}
