package com.pebbledb.events;

import com.lmax.disruptor.EventHandler;
import com.pebbledb.actions.node.*;

public class DatabaseEventHandler implements EventHandler<ExchangeEvent> {

    public void onEvent(ExchangeEvent event, long sequence, boolean endOfBatch) {

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

        switch (event.getAction()) {
            case GET_NODE:
                GetNode.handle(event);
                break;
            case POST_NODE:
                PostNode.handle(event);
                break;
            case DELETE_NODE:
                DeleteNode.handle(event);
                break;
            case PUT_NODE_PROPERTIES:
                PutNodeProperties.handle(event);
                break;
            case DELETE_NODE_PROPERTIES:
                DeleteNodeProperties.handle(event);
                break;
            case GET_NODE_PROPERTY:
                GetNodeProperty.handle(event);
                break;
            case PUT_NODE_PROPERTY:
                PutNodeProperty.handle(event);
                break;
            case DELETE_NODE_PROPERTY:
                DeleteNodeProperty.handle(event);
                break;

        }

    }
}
