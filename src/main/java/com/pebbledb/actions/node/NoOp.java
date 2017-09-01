package com.pebbledb.actions.node;

import com.pebbledb.events.ExchangeEvent;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;

public class NoOp {
    public static void handle(ExchangeEvent exchangeEvent, int number) {
        HttpServerExchange exchange = exchangeEvent.get();
        exchange.setStatusCode(StatusCodes.OK);
        exchangeEvent.clear();
    }

}
