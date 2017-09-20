package com.pebbledb.graphs;

import java.util.*;

public interface Graph {

    void clear();

    // Relationship Types
    Set<String> getRelationshipTypes();
    Map<String, Integer> getRelationshipTypesCount();
    Integer getRelationshipTypeCount(String type);

    // Nodes
    boolean addNode(String key);
    boolean addNode(String key, Map<String, Object> properties);
    boolean removeNode(String key);
    Map<String, Object> getNode(String key);
    int getNodeId(String key);

    // Node Properties
    Object getNodeProperty(String key, String property);
    boolean updateNodeProperties(String key, Map<String, Object> properties);
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
    List<Map<String, Object>> getOutgoingRelationships(String from);
    List<Map<String, Object>> getOutgoingRelationships(int from);
    List<Map<String, Object>> getOutgoingRelationships(String type, String from);
    List<Map<String, Object>> getOutgoingRelationships(String type, int from);
    List<Map<String, Object>> getIncomingRelationships(String from);
    List<Map<String, Object>> getIncomingRelationships(int from);
    List<Map<String, Object>> getIncomingRelationships(String type, String from);
    List<Map<String, Object>> getIncomingRelationships(String type, int from);

    List<Integer> getOutgoingRelationshipNodeIds(String type, String from);
    List<Integer> getOutgoingRelationshipNodeIds(String type, Integer from);
    Object[] getOutgoingRelationshipNodes(String type, String from);
    Object[] getIncomingRelationshipNodes(String type, String to);

    List<Integer> getIncomingRelationshipNodeIds(String type, String to);
    List<Integer> getIncomingRelationshipNodeIds(String type, Integer to);
    Object[] getOutgoingRelationshipNodes(String type, Integer from);
    Object[] getIncomingRelationshipNodes(String type, Integer to);

    // Extras
    Iterator<Map<String, Object>> getAllNodes();

}
