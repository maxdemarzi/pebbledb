package com.pebbledb.events;

import com.lmax.disruptor.EventHandler;
import com.pebbledb.server.NodeHandler;

public class DatabaseEventHandler implements EventHandler<ExchangeEvent> {

    public void onEvent(ExchangeEvent event, long sequence, boolean endOfBatch) {
       // HttpServerExchange exchange = event.get();
        /*
            Paths:
            /db/node/:id
            /db/node/:id/degree/:direction
            /db/node/:id/property/:key
            /db/node/:id/properties
            /db/relationship/:type/:from/:to/:number
            /db/relationship/:type/:from/:to/property/:key
            /db/relationship/:type/:from/:to/properties
            /db/relationship/:type/:from/:to/:number/property/:key
            /db/relationship/:type/:from/:to/:number/properties

         */

        if (event.getPath().startsWith("/db/node")){
            NodeHandler.handle(event);
        } else {
           // RelationshipHandler.handle(event);
        }

//
//        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
//        exchange.getResponseSender().send("Hello World " + sequence
//                + " path: " + event.getPath()
//                + " body: " + event.getBody());
    }
}
