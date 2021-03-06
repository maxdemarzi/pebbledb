package com.pebbledb.server;

import com.pebbledb.actions.Action;
import com.pebbledb.events.ExchangeEvent;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.PathTemplateMatch;

public class RequestHandler implements HttpHandler {
    private boolean write;
    private Action action;

    RequestHandler(boolean write, Action action) {
        this.write = write;
        this.action = action;
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {

        final long seq = PebbleServer.ringBuffer.next();
        final ExchangeEvent exchangeEvent = PebbleServer.ringBuffer.get(seq);

        exchangeEvent.setRequest(write, action, exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY).getParameters());

        exchangeEvent.set(exchange);
        PebbleServer.ringBuffer.publish(seq);
        // This is deprecated but it works...
        exchange.dispatch();
    }
}