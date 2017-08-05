package com.pebbledb.events;

import com.lmax.disruptor.EventHandler;
import com.pebbledb.actions.node.DeleteNode;
import com.pebbledb.actions.node.*;
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
    private final int number;

    public DatabaseEventHandler(int number) {
        this.number = number;
    }

    public void onEvent(ExchangeEvent event, long sequence, boolean endOfBatch) {
        boolean respond = event.isResponder(number);
        //System.out.println("number: " + number +  " write: " + event.getWrite() + " Thread id:" + Thread.currentThread().getId() + " this: " + this.hashCode()  + " sequence: " + sequence + " action: " + event.getAction().name() + " respond: " + respond);
        if (event.getWrite() ) {
            switch (event.getAction()) {
                case POST_NODE:
                    PostNode.handle(event, number, respond);
                    break;
                case DELETE_NODE:
                    DeleteNode.handle(event, number, respond);
                    break;

                case PUT_NODE_PROPERTIES:
                    PutNodeProperties.handle(event, number, respond);
                    break;
                case DELETE_NODE_PROPERTIES:
                    DeleteNodeProperties.handle(event, number, respond);
                    break;
                case PUT_NODE_PROPERTY:
                    PutNodeProperty.handle(event, number, respond);
                    break;
                case DELETE_NODE_PROPERTY:
                    DeleteNodeProperty.handle(event, number, respond);
                    break;

                case POST_RELATIONSHIP:
                    PostRelationship.handle(event, number, respond);
                    break;
                case DELETE_RELATIONSHIP:
                    DeleteRelationship.handle(event, number, respond);
                    break;

                case PUT_RELATIONSHIP_PROPERTIES:
                    PutRelationshipProperties.handle(event, number, respond);
                    break;
                case DELETE_RELATIONSHIP_PROPERTIES:
                    DeleteRelationshipProperties.handle(event, number, respond);
                    break;
                case PUT_RELATIONSHIP_PROPERTY:
                    PutRelationshipProperty.handle(event, number, respond);
                    break;
                case DELETE_RELATIONSHIP_PROPERTY:
                    DeleteRelationshipProperty.handle(event, number, respond);
                    break;
            }
        } else if (respond) {
            switch (event.getAction()) {
                case NOOP:
                    NoOp.handle(event, number);
                    break;
                case GET_RELATIONSHIP_TYPES:
                    GetRelationshipTypes.handle(event, number);
                    break;
                case GET_RELATIONSHIP_TYPES_COUNT:
                    GetRelationshipTypesCount.handle(event, number);
                    break;
                case GET_RELATIONSHIP_TYPE_COUNT:
                    GetRelationshipTypeCount.handle(event, number);
                    break;

                case GET_NODE:
                    GetNode.handle(event, number);
                    break;
                case GET_NODE_PROPERTY:
                    GetNodeProperty.handle(event, number);
                    break;
                case GET_RELATIONSHIP:
                    GetRelationship.handle(event, number);
                    break;
                case GET_RELATIONSHIP_PROPERTY:
                    GetRelationshipProperty.handle(event, number);
                    break;

            }

        }

    }
}
