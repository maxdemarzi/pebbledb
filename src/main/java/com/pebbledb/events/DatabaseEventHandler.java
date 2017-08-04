package com.pebbledb.events;

import com.lmax.disruptor.EventHandler;
import com.pebbledb.actions.NotImplementedYet;
import com.pebbledb.actions.node.*;
import com.pebbledb.actions.node_properties.*;
import com.pebbledb.actions.relationship.*;
import com.pebbledb.actions.relationship_properties.DeleteRelationshipProperties;
import com.pebbledb.actions.relationship_properties.GetRelationshipProperty;
import com.pebbledb.actions.relationship_type.GetRelationshipTypeCount;
import com.pebbledb.actions.relationship_type.GetRelationshipTypes;
import com.pebbledb.actions.relationship_type.GetRelationshipTypesCount;

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

            case GET_RELATIONSHIP_TYPES:
                GetRelationshipTypes.handle(event);
                break;
            case GET_RELATIONSHIP_TYPES_COUNT:
                GetRelationshipTypesCount.handle(event);
                break;
            case GET_RELATIONSHIP_TYPE_COUNT:
                GetRelationshipTypeCount.handle(event);
                break;

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

            case GET_RELATIONSHIP:
                GetRelationship.handle(event);
                break;
            case POST_RELATIONSHIP:
                PostRelationship.handle(event);
                break;
            case DELETE_RELATIONSHIP:
                DeleteRelationship.handle(event);
                break;
            case PUT_RELATIONSHIP_PROPERTIES:
                //PutRelationshipProperties.handle(event);
                NotImplementedYet.handle(event);
                break;
            case DELETE_RELATIONSHIP_PROPERTIES:
                DeleteRelationshipProperties.handle(event);
                break;
            case GET_RELATIONSHIP_PROPERTY:
                GetRelationshipProperty.handle(event);
                break;
            case PUT_RELATIONSHIP_PROPERTY:
                //PutRelationshipProperty.handle(event);
                NotImplementedYet.handle(event);
                break;
            case DELETE_RELATIONSHIP_PROPERTY:
                //DeleteRelationshipProperty.handle(event);
                NotImplementedYet.handle(event);
                break;

        }

    }
}
