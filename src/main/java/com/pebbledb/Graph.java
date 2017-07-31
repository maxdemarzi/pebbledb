package com.pebbledb;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Graph {

    void clear();

    // Relationship Types
    Set<String> getRelationshipTypes();
    Map<String, Integer> getRelationshipTypesCount();
    Integer getRelationshipTypeCount(String relType);

    // Nodes
    boolean addNode(String key);
    boolean addNode(String key, Map<String, Object> properties);
    boolean removeNode(String key);
    Map<String, Object> getNode(String key);
    int getNodeId(String key);

    // Node Properties
    Object getNodeProperty(String key, String property);
    boolean updateNodeProperties(String key, Map properties);
    boolean deleteNodeProperties(String key);
    boolean updateNodeProperty(String key, String property, Object value);
    boolean deleteNodeProperty(String key, String property);

    // Relationships
    boolean addRelationship(String type, String from, String to);
    boolean addRelationship(String type, String from, String to, Map<String, Object> properties);
    boolean removeRelationship(String type, String from, String to);
    boolean removeRelationship(String type, String from, String to, int number);
    Map<String, Object> getRelationship(String type, String from, String to);
    Map<String, Object> getRelationship(String type, String from, String to, int number);

    // Relationship Properties
    Object getRelationshipProperty(String type, String from, String to, String property);
    Object getRelationshipProperty(String type, String from, String to, int number, String property);
    boolean updateRelationshipProperties(String type, String from, String to, Map<String, Object> properties);
    boolean updateRelationshipProperties(String type, String from, String to, int number, Map<String, Object> properties);
    boolean deleteRelationshipProperties(String type, String from, String to);
    boolean deleteRelationshipProperties(String type, String from, String to, int number);
    boolean updateRelationshipProperty(String type, String from, String to, String property, Object value);
    boolean updateRelationshipProperty(String type, String from, String to, int number, String property, Object value);
    boolean deleteRelationshipProperty(String type, String from, String to, String property);
    boolean deleteRelationshipProperty(String type, String from, String to, int number, String property);

    // Node Degree
    int getNodeDegree(String key);
    int getNodeDegree(String key, String direction);
    int getNodeDegree(String key, String direction, List<String> types);

    // Traversing
    Collection<Integer> getOutgoingRelationshipNodeIds(String type, String from);
    Collection<Integer> getOutgoingRelationshipNodeIds(String type, Integer from);
    Object[] getOutgoingRelationshipNodes(String type, String from);
    Object[] getIncomingRelationshipNodes(String type, String to);

    Collection<Integer> getIncomingRelationshipNodeIds(String type, String to);
    Collection<Integer> getIncomingRelationshipNodeIds(String type, Integer to);
    Object[] getOutgoingRelationshipNodes(String type, Integer from);
    Object[] getIncomingRelationshipNodes(String type, Integer to);


}
