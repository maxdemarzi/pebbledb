package com.pebbledb.events;

import io.undertow.server.HttpServerExchange;

public class ExchangeEvent {

    private HttpServerExchange exchange;
    private boolean write;
    private String path;
    private String body;

    public void set(HttpServerExchange exchange) {
        this.exchange = exchange;
    }

    public HttpServerExchange get() {
        return this.exchange;
    }

    void setRequest(boolean write, String path, String body) {
        this.write = write;
        this.path = path;
        this.body = body;
    }

    public String getPath() {
        return path;
    }

    public String getBody() {
        return body;
    }

    public boolean getWrite() {
        return write;
    }

    void clear() {
        exchange.endExchange();
        exchange = null;
    }
}