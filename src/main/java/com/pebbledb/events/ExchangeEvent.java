package com.pebbledb.events;

import io.undertow.server.HttpServerExchange;

import java.util.Map;

public class ExchangeEvent {

    private HttpServerExchange exchange;
    private boolean write;
    private String path;
    private Map params;
    private String body;

    public void set(HttpServerExchange exchange) {
        this.exchange = exchange;
    }

    public HttpServerExchange get() {
        return this.exchange;
    }

    public void setRequest(boolean write, String path, Map params, String body) {
        this.write = write;
        this.path = path;
        this.params = params;
        this.body = body;
    }

    public String getPath() {
        return path;
    }

    public Map getParams() {
        return params;
    }

    public String getBody() {
        return body;
    }

    public void clear() {
        exchange.endExchange();
        exchange = null;
    }
}