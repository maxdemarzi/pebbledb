package com.pebbledb.events;

import com.pebbledb.actions.Action;
import io.undertow.server.HttpServerExchange;

import java.util.Map;

public class ExchangeEvent {

    private HttpServerExchange exchange;
    private boolean write;
    private Action action;
    private Map parameters;
    private String body;

    public void set(HttpServerExchange exchange) {
        this.exchange = exchange;
    }

    public HttpServerExchange get() {
        return this.exchange;
    }

    public void setRequest(boolean write, Action action, Map parameters) {
        this.write = write;
        this.action = action;
        this.parameters = parameters;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Action getAction() {
        return action;
    }

    public Map getParameters() {
        return parameters;
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