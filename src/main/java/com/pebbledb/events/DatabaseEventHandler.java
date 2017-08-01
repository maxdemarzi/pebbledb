package com.pebbledb.events;

import com.lmax.disruptor.EventHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class DatabaseEventHandler implements EventHandler<ExchangeEvent> {

    public void onEvent(ExchangeEvent event, long sequence, boolean endOfBatch) {
        HttpServerExchange exchange = event.get();


        //System.out.println("in handle event " + sequence);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send("Hello World " + sequence
                + " path: " + event.getPath()
                + " params: " + event.getParams().toString()
                + " body: " + event.getBody());
        //exchange.endExchange();
    }
}
