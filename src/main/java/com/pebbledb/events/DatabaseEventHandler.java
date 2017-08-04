package com.pebbledb.events;

import com.lmax.disruptor.EventHandler;
import com.pebbledb.actions.node.DeleteNode;
import com.pebbledb.actions.node.GetNode;
import com.pebbledb.actions.node.PostNode;
import com.pebbledb.actions.node_properties.*;
import com.pebbledb.actions.relationship.DeleteRelationship;
import com.pebbledb.actions.relationship.GetRelationship;
import com.pebbledb.actions.relationship.PostRelationship;
import com.pebbledb.actions.relationship_properties.*;
import com.pebbledb.actions.relationship_type.GetRelationshipTypeCount;
import com.pebbledb.actions.relationship_type.GetRelationshipTypes;
import com.pebbledb.actions.relationship_type.GetRelationshipTypesCount;

public class DatabaseEventHandler implements EventHandler<ExchangeEvent> {

    public void onEvent(ExchangeEvent event, long sequence, boolean endOfBatch) {

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
                PutRelationshipProperties.handle(event);
                break;
            case DELETE_RELATIONSHIP_PROPERTIES:
                DeleteRelationshipProperties.handle(event);
                break;
            case GET_RELATIONSHIP_PROPERTY:
                GetRelationshipProperty.handle(event);
                break;
            case PUT_RELATIONSHIP_PROPERTY:
                PutRelationshipProperty.handle(event);
                break;
            case DELETE_RELATIONSHIP_PROPERTY:
                DeleteRelationshipProperty.handle(event);
                break;

        }

    }
}
