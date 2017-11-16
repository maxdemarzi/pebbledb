package com.pebbledb.graphs;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.*;
import org.roaringbitmap.RoaringBitmap;

import java.util.*;

public class FastUtilGraph implements Graph {

    private Object2ObjectArrayMap<String, Object2IntOpenHashMap<String>> nodeKeys;
    private ObjectArrayList<Map<String, Object>> nodes;
    private Object2IntOpenHashMap<String> relationshipKeys;
    private ObjectArrayList<Map<String, Object>> relationships;
    private Object2ObjectOpenHashMap<String, ReversibleMultiMap> related;
    private Object2IntArrayMap<String> relationshipCounts;
    private Object2ObjectArrayMap<String, Long2IntOpenHashMap> relatedCounts2;
    private RoaringBitmap deletedNodes;
    private RoaringBitmap deletedRelationships;

    public FastUtilGraph() {
        nodeKeys = new Object2ObjectArrayMap<>();
        nodes = new ObjectArrayList<>();
        relationshipKeys = new Object2IntOpenHashMap<>();
        relationshipKeys.defaultReturnValue(-1);
        relationships = new ObjectArrayList<>();
        related = new Object2ObjectOpenHashMap<>();
        relationshipCounts = new Object2IntArrayMap<>();
        relationshipCounts.defaultReturnValue(0);
        relatedCounts2 = new Object2ObjectArrayMap<>();
        deletedNodes = new RoaringBitmap();
        deletedRelationships = new RoaringBitmap();
    }

    public void clear() {
        nodeKeys.clear();
        nodes.clear();
        relationships.clear();
        related.clear();
        relationshipCounts.clear();
        relatedCounts2.clear();
        deletedNodes.clear();
        deletedRelationships.clear();
    }

    // Relationship Types
    public Set<String> getRelationshipTypes() {
        return related.keySet();
    }

    public Map<String, Integer> getRelationshipTypesCount() {
        return relationshipCounts;
    }

    public Integer getRelationshipTypeCount(String type) {
        return relationshipCounts.getInt(type);
    }

    private Object2IntOpenHashMap<String> getOrCreateNodeKey(String label) {
        Object2IntOpenHashMap<String> nodeKey;

        if (!nodeKeys.containsKey(label)) {
            nodeKey = new Object2IntOpenHashMap<>();
            nodeKey.defaultReturnValue(-1);
            nodeKeys.put(label, nodeKey);
        } else {
            nodeKey = nodeKeys.get(label);
        }
        return nodeKey;
    }
    private Object2IntOpenHashMap<String> getNodeKey(String label) {
        Object2IntOpenHashMap<String> nodeKey;

        if (!nodeKeys.containsKey(label)) {
            nodeKey = new Object2IntOpenHashMap<>();
            nodeKey.defaultReturnValue(-1);
        } else {
            nodeKey = nodeKeys.get(label);
        }
        return nodeKey;
    }
    
    // Nodes
    public boolean addNode (String label, String key) {
        Object2IntOpenHashMap<String> nodeKey = getOrCreateNodeKey(label);
        
        if (nodeKey.containsKey(key)) {
            return false;
        } else {
            int nodeId;
            if (deletedNodes.isEmpty()) {
                nodes.add(new HashMap<>());
                nodeId = nodes.size() - 1;
                nodeKey.put(key, nodeId);
            } else {
                nodeId = deletedNodes.first();
                nodes.set(nodeId, new HashMap<>());
                nodeKey.put(key, nodeId);
                deletedNodes.remove(nodeId);
            }
        }
        return true;
    }

    public boolean addNode (String label, String key, Map<String, Object> properties) {
        Object2IntOpenHashMap<String> nodeKey = getOrCreateNodeKey(label);
        
        if (nodeKey.containsKey(key)) {
            return false;
        } else {
            int nodeId;
            if (deletedNodes.isEmpty()) {
                nodes.add(properties);
                nodeId = nodes.size() -1;
                nodeKey.put(key, nodeId);
            } else {
                nodeId = deletedNodes.first();
                nodes.set(nodeId, properties);
                nodeKey.put(key, nodeId);
                deletedNodes.remove(nodeId);
            }

            return true;
        }
    }

    public boolean removeNode(String label, String key) {
        Object2IntOpenHashMap<String> nodeKey = getNodeKey(label);
        if(!nodeKey.containsKey(key)) {
            return false;
        }

        int id = nodeKey.getInt(key);
        nodes.set(id, null);
        deletedNodes.add(id);

        for (String type : related.keySet()) {
            ReversibleMultiMap rels = related.get(type);
            int outgoingCount = 0;
            int incomingCount = 0;
            for (Integer value : rels.getRels(id)) {
                outgoingCount++;
                relationships.set(value, null);
                deletedRelationships.add(value);
            }
            for (Integer value : rels.getRelsByValue(id)) {
                incomingCount++;
                relationships.set(value, null);
                deletedRelationships.add(value);
            }
            rels.removeAll(id);
            relationshipCounts.put(type, relationshipCounts.getInt(type) - (outgoingCount + incomingCount));
        }
        nodeKey.removeInt(key);

        return true;
    }

    public Map<String, Object> getNode(String label, String key) {
        Object2IntOpenHashMap<String> nodeKey = getNodeKey(label);
        int id = nodeKey.getInt(key);
        if (id == -1) { return null; }
        return nodes.get(nodeKey.getInt(key));
    }

    public int getNodeId(String label, String key) {
        Object2IntOpenHashMap<String> nodeKey = getNodeKey(label);
        return nodeKey.getInt(key);
    }

    // Node Properties
    public Object getNodeProperty(String label, String key, String property) {
        Object2IntOpenHashMap<String> nodeKey = getNodeKey(label);
        int id = nodeKey.getInt(key);
        if (id == -1) { return null; }
        return nodes.get(id).get(property);
    }

    public boolean updateNodeProperties(String label, String key, Map<String, Object> properties) {
        Object2IntOpenHashMap<String> nodeKey = getNodeKey(label);
        int id = nodeKey.getInt(key);
        if (id == -1) { return false; }
        Map<String, Object> current = nodes.get(id);
        current.putAll(properties);
        return true;
    }

    public boolean deleteNodeProperties(String label, String key) {
        Object2IntOpenHashMap<String> nodeKey = getNodeKey(label);
        int id = nodeKey.getInt(key);
        if (id == -1) { return false; }
        nodes.add(id, new HashMap<>());
        return true;
    }

    public boolean updateNodeProperty(String label, String key, String property, Object value) {
        Object2IntOpenHashMap<String> nodeKey = getNodeKey(label);
        int id = nodeKey.getInt(key);
        if (id == -1) { return false; }
        Map<String, Object> properties = nodes.get(id);
        properties.put(property, value);
        return true;
    }

    public boolean deleteNodeProperty(String label, String key, String property) {
        Object2IntOpenHashMap<String> nodeKey = getNodeKey(label);
        int id = nodeKey.getInt(key);
        if (id == -1) { return false; }
        Map<String, Object> properties = nodes.get(id);
        properties.remove(property);
        return true;
    }

    // Relationships
    public boolean addRelationship(String type, String label1, String from, String label2, String to) {
        Object2IntOpenHashMap<String> node1Key = getNodeKey(label1);
        Object2IntOpenHashMap<String> node2Key = getNodeKey(label2);
        
        related.putIfAbsent(type, new ReversibleMultiMap());
        relationshipCounts.putIfAbsent(type, 0);
        relationshipCounts.put(type, relationshipCounts.getInt(type) + 1);

        int node1 = node1Key.getInt(from);
        int node2 = node2Key.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        relatedCounts2.putIfAbsent(type, new Long2IntOpenHashMap());
        Long2IntOpenHashMap relatedCount = relatedCounts2.get(type);
        long countId = ((long)node1 << 32) + node2;
        int count = relatedCount.get(countId) + 1;
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("_incoming_node_id", node1);
        properties.put("_outgoing_node_id", node2);

        relationships.add(properties);
        relatedCount.put(countId, count);
        related.get(type).put(node1, node2, relationships.size() -1);
        relationshipKeys.put(node1 + "-" + node2 + "-" + type + "-" + count, relationships.size() - 1);

        return true;
    }

    public boolean addRelationship(String type, String label1, String from, String label2, String to, Map<String, Object> properties) {
        Object2IntOpenHashMap<String> node1Key = getNodeKey(label1);
        Object2IntOpenHashMap<String> node2Key = getNodeKey(label2);

        related.putIfAbsent(type, new ReversibleMultiMap());
        relationshipCounts.putIfAbsent(type, 0);
        relationshipCounts.put(type, relationshipCounts.getInt(type) + 1);

        int node1 = node1Key.getInt(from);
        int node2 = node2Key.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }
        properties.put("_incoming_node_id", node1);
        properties.put("_outgoing_node_id", node2);

        relatedCounts2.putIfAbsent(type, new Long2IntOpenHashMap());
        Long2IntOpenHashMap relatedCount = relatedCounts2.get(type);
        long countId = ((long)node1 << 32) + node2;
        int count = relatedCount.get(countId) + 1;

        relationships.add(properties);
        relatedCount.put(countId, count);
        related.get(type).put(node1, node2, relationships.size() - 1);
        relationshipKeys.put(node1 + "-" + node2 + "-" + type + "-" + count, relationships.size() - 1);

        return true;
    }

    public boolean removeRelationship (String type, String label1, String from, String label2, String to) {
        Object2IntOpenHashMap<String> node1Key = getNodeKey(label1);
        Object2IntOpenHashMap<String> node2Key = getNodeKey(label2);

        if(!related.containsKey(type)) {
            return false;
        }
        int node1 = node1Key.getInt(from);
        int node2 = node2Key.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }
        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts2.get(type).get(countId);
        if (count == 0) {
            return false;
        }
        relatedCounts2.get(type).put(countId, count - 1);
        relationshipCounts.put(type, relationshipCounts.getInt(type) - 1);
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-" + count);
        related.get(type).removeRelationship(node1, node2, relId);
        relationships.set(relId, null);
        relationshipKeys.removeInt(node1 + "-" + node2 + "-" + type + "-" + count);

        return true;
    }

    public boolean removeRelationship(String type, String label1, String from, String label2, String to, int number) {
        Object2IntOpenHashMap<String> node1Key = getNodeKey(label1);
        Object2IntOpenHashMap<String> node2Key = getNodeKey(label2);

        if(!related.containsKey(type)) {
            return false;
        }
        int node1 = node1Key.getInt(from);
        int node2 = node2Key.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }
        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts2.get(type).get(countId);
        if (count == 0 || count < number) {
            return false;
        }
        relatedCounts2.get(type).put(countId, count - 1);
        relationshipCounts.put(type, relationshipCounts.getInt(type) - 1);
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-" + number);
        related.get(type).removeRelationship(node1, node2, relId);
        relationships.set(relId, null);
        if (count == 1) {
            relationshipKeys.removeInt(node1 + "-" + node2 + "-" + type + "-" + number);
        } else {
            if (count != number) {
                relationshipKeys.put(node1 + "-" + node2 + "-" + type + "-" + number,
                        relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-" + count));
            }
            relationshipKeys.removeInt(node1 + "-" + node2 + "-" + type + "-" + count);
        }

        return true;
    }

    public Map<String, Object> getRelationship(String type, String label1, String from, String label2, String to) {
        Object2IntOpenHashMap<String> node1Key = getNodeKey(label1);
        Object2IntOpenHashMap<String> node2Key = getNodeKey(label2);

        int node1 = node1Key.getInt(from);
        int node2 = node2Key.getInt(to);
        if (node1 == -1 || node2 == -1) { return null; }
        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts2.get(type).get(countId);
        if (count == 0) { return null; }
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-1");

        return relationships.get(relId);
    }

    public Map<String, Object> getRelationship(String type, String label1, String from, String label2, String to, int number) {
        Object2IntOpenHashMap<String> node1Key = getNodeKey(label1);
        Object2IntOpenHashMap<String> node2Key = getNodeKey(label2);

        int node1 = node1Key.getInt(from);
        int node2 = node2Key.getInt(to);
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-" + number);

        return relationships.get(relId);
    }

    // Relationship Properties

    public Object getRelationshipProperty(String type, String label1, String from, String label2, String to, String property) {
        Object2IntOpenHashMap<String> node1Key = getNodeKey(label1);
        Object2IntOpenHashMap<String> node2Key = getNodeKey(label2);

        int node1 = node1Key.getInt(from);
        int node2 = node2Key.getInt(to);
        if (node1 == -1 || node2 == -1) { return null; }

        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts2.get(type).get(countId);
        if (count == 0) { return null; }
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-1");
        return relationships.get(relId).get(property);
    }

    public Object getRelationshipProperty(String type, String label1, String from, String label2, String to, int number, String property) {
        Object2IntOpenHashMap<String> node1Key = getNodeKey(label1);
        Object2IntOpenHashMap<String> node2Key = getNodeKey(label2);

        int node1 = node1Key.getInt(from);
        int node2 = node2Key.getInt(to);
        if (node1 == -1 || node2 == -1) { return null; }

        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts2.get(type).get(countId);
        if (count == 0 || count < number) { return null; }
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-" + number);
        return relationships.get(relId).get(property);
    }

    public boolean updateRelationshipProperties(String type, String label1, String from, String label2, String to, Map<String, Object> properties) {
        Object2IntOpenHashMap<String> node1Key = getNodeKey(label1);
        Object2IntOpenHashMap<String> node2Key = getNodeKey(label2);

        int node1 = node1Key.getInt(from);
        int node2 = node2Key.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts2.get(type).get(countId);
        if (count == 0) { return false; }
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-1");
        relationships.set(relId, properties);
        return true;
    }


    public boolean updateRelationshipProperties(String type, String label1, String from, String label2, String to, int number, Map<String, Object> properties) {
        Object2IntOpenHashMap<String> node1Key = getNodeKey(label1);
        Object2IntOpenHashMap<String> node2Key = getNodeKey(label2);

        int node1 = node1Key.getInt(from);
        int node2 = node2Key.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts2.get(type).get(countId);
        if (count == 0 || count < number) { return false; }
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-" + number);
        relationships.set(relId, properties);
        return true;
    }

    public boolean deleteRelationshipProperties(String type, String label1, String from, String label2, String to) {
        Object2IntOpenHashMap<String> node1Key = getNodeKey(label1);
        Object2IntOpenHashMap<String> node2Key = getNodeKey(label2);

        int node1 = node1Key.getInt(from);
        int node2 = node2Key.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts2.get(type).get(countId);
        if (count == 0 ) { return false; }
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-1");
        relationships.set(relId, new HashMap<>());
        return true;
    }

    public boolean deleteRelationshipProperties(String type, String label1, String from, String label2, String to, int number) {
        Object2IntOpenHashMap<String> node1Key = getNodeKey(label1);
        Object2IntOpenHashMap<String> node2Key = getNodeKey(label2);

        int node1 = node1Key.getInt(from);
        int node2 = node2Key.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts2.get(type).get(countId);
        if (count == 0 || count < number) { return false; }
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-" + number);
        relationships.set(relId, new HashMap<>());
        return true;
    }

    public boolean updateRelationshipProperty(String type, String label1, String from, String label2, String to, String property, Object value) {
        Object2IntOpenHashMap<String> node1Key = getNodeKey(label1);
        Object2IntOpenHashMap<String> node2Key = getNodeKey(label2);

        int node1 = node1Key.getInt(from);
        int node2 = node2Key.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }
        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts2.get(type).get(countId);
        if (count == 0) { return false; }
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-1");
        relationships.get(relId).put(property, value);
        return true;
    }

    public boolean updateRelationshipProperty(String type, String label1, String from, String label2, String to, int number, String property, Object value) {
        Object2IntOpenHashMap<String> node1Key = getNodeKey(label1);
        Object2IntOpenHashMap<String> node2Key = getNodeKey(label2);

        int node1 = node1Key.getInt(from);
        int node2 = node2Key.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts2.get(type).get(countId);
        if (count == 0 || count < number) { return false; }
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-" + number);
        relationships.get(relId).put(property, value);
        return true;
    }

    public boolean deleteRelationshipProperty(String type, String label1, String from, String label2, String to, String property) {
        Object2IntOpenHashMap<String> node1Key = getNodeKey(label1);
        Object2IntOpenHashMap<String> node2Key = getNodeKey(label2);

        int node1 = node1Key.getInt(from);
        int node2 = node2Key.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts2.get(type).get(countId);
        if (count == 0) { return false; }
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-1");

        Map<String, Object> properties = relationships.get(relId);
        if (properties.containsKey(property)) {
            properties.remove(property);
            return true;
        }
        return false;
    }

    public boolean deleteRelationshipProperty(String type, String label1, String from, String label2, String to, int number, String property) {
        Object2IntOpenHashMap<String> node1Key = getNodeKey(label1);
        Object2IntOpenHashMap<String> node2Key = getNodeKey(label2);

        int node1 = node1Key.getInt(from);
        int node2 = node2Key.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        long countId = ((long)node1 << 32) + node2;
        int count = relatedCounts2.get(type).get(countId);
        if ( count == 0 || count < number) { return false; }
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-" + number);

        Map<String, Object> properties = relationships.get(relId);
        if (properties.containsKey(property)) {
            properties.remove(property);
            return true;
        }
        return true;
    }

    // Degrees
    public int getNodeDegree(String label, String identifier) {
        return getNodeDegree(label, identifier, "all", new ArrayList<>());
    }

    public int getNodeDegree(String label, String identifier, String direction) {
        return getNodeDegree(label, identifier, direction, new ArrayList<>());
    }

    public int getNodeDegree(String label, String key, String direction, List<String> types) {
        Object2IntOpenHashMap<String> nodeKey = getNodeKey(label);

        int id = nodeKey.getInt(key);
        if (id == -1) { return -1; }

        int count = 0;
        List<String> relTypes;
        if (types.isEmpty()) {
            relTypes = new ArrayList<>(related.keySet());
        } else {
            types.retainAll(related.keySet());
            relTypes = types;
        }

        for (String type : relTypes) {
            ReversibleMultiMap rels = related.get(type);
            if (direction.equals("all") || direction.equals("out")) {
                count += rels.getFromSize(id);
            }
            if (direction.equals("all") || direction.equals("in")) {
                count += rels.getToSize(id);
            }
        }
        return count;
    }

    // Traversing
    public List<Map<String, Object>> getOutgoingRelationships(String label, String from) {
        Object2IntOpenHashMap<String> nodeKey = getNodeKey(label);
        int node1 = nodeKey.getInt(from);
        return getOutgoingRelationships(node1);
    }

    public List<Map<String, Object>> getOutgoingRelationships(int node1) {
        List<Map<String,Object>> nodeRelationships = new ArrayList<>();
        for (String type : related.keySet()) {
            for (Integer rel : related.get(type).getRels(node1)) {
                nodeRelationships.add(relationships.get(rel));
            }
        }
        return nodeRelationships;
    }

    public List<Map<String,Object>> getOutgoingRelationships(String type, String label, String from) {
        Object2IntOpenHashMap<String> nodeKey = getNodeKey(label);
        int node1 = nodeKey.getInt(from);
        return getOutgoingRelationships(type, node1);
    }

    public List<Map<String,Object>> getOutgoingRelationships(String type, int node) {
        List<Map<String,Object>> nodeRelationships = new ArrayList<>();
        for (Integer rel : related.get(type).getRels(node)) {
            nodeRelationships.add(relationships.get(rel));
        }
        return nodeRelationships;
    }

    public List<Map<String,Object>> getIncomingRelationships(String label, String to) {
        Object2IntOpenHashMap<String> nodeKey = getNodeKey(label);
        int node2 = nodeKey.getInt(to);
        return getIncomingRelationships(node2);
    }

    public List<Map<String,Object>> getIncomingRelationships(int node2) {
        List<Map<String,Object>> nodeRelationships = new ArrayList<>();
        for (String type : related.keySet()) {
            for (Integer rel : related.get(type).getRelsByValue(node2)) {
                nodeRelationships.add(relationships.get(rel));
            }
        }
        return nodeRelationships;
    }

    public List<Map<String,Object>> getIncomingRelationships(String type, String label, String to) {
        Object2IntOpenHashMap<String> nodeKey = getNodeKey(label);
        int node2 = nodeKey.getInt(to);
        return getIncomingRelationships(type, node2);
    }

    public List<Map<String,Object>> getIncomingRelationships(String type, int node) {
        List<Map<String,Object>> nodeRelationships = new ArrayList<>();
        for (Integer rel : related.get(type).getRelsByValue(node)) {
            nodeRelationships.add(relationships.get(rel));
        }
        return nodeRelationships;
    }

    public Object[] getOutgoingRelationshipNodes(String type, String label, String from) {
        Object2IntOpenHashMap<String> nodeKey = getNodeKey(label);
        int node1 = nodeKey.getInt(from);

        List<Integer> nodeIds = (List<Integer>)related.get(type).getNodes(node1);
        int size = nodeIds.size();
        Object[] nodeArray = new Object[size];
        for(int i=-1; ++i < size;) {
            nodeArray[i] = nodes.get(nodeIds.get(i));
        }
        return nodeArray;
    }

    public Object[] getIncomingRelationshipNodes(String type, String label, String to) {
        Object2IntOpenHashMap<String> nodeKey = getNodeKey(label);
        int node2 = nodeKey.getInt(to);

        List<Integer> nodeIds = (List<Integer>)related.get(type).getNodesByValue(node2);
        int size = nodeIds.size();
        Object[] nodeArray = new Object[size];
        for(int i=-1; ++i < size;) {
            nodeArray[i] = nodes.get(nodeIds.get(i));
        }
        return nodeArray;
    }

    public Object[] getOutgoingRelationshipNodes(String type, Integer from) {
        List<Integer> nodeIds = (List<Integer>)related.get(type).getNodes(from);
        int size = nodeIds.size();
        Object[] nodeArray = new Object[size];
        for(int i=-1; ++i < size;) {
            nodeArray[i] = nodes.get(nodeIds.get(i));
        }
        return nodeArray;
    }

    public Object[] getIncomingRelationshipNodes(String type, Integer to) {
        List<Integer> nodeIds = (List<Integer>)related.get(type).getNodesByValue(to);
        int size = nodeIds.size();
        Object[] nodeArray = new Object[size];
        for(int i=-1; ++i < size;) {
            nodeArray[i] = nodes.get(nodeIds.get(i));
        }
        return nodeArray;
    }

    public List<Integer> getOutgoingRelationshipNodeIds(String type, Integer from) {
        return (List<Integer>)related.get(type).getNodes(from);
    }

    public List<Integer> getOutgoingRelationshipNodeIds(String type, String label, String from) {
        Object2IntOpenHashMap<String> nodeKey = getNodeKey(label);
        int node1 = nodeKey.getInt(from);
        return (List<Integer>)related.get(type).getNodes(node1);
    }

    public List<Integer> getIncomingRelationshipNodeIds(String type, Integer to) {
        return (List<Integer>)related.get(type).getNodesByValue(to);
    }

    public List<Integer> getIncomingRelationshipNodeIds(String type, String label, String to) {
        Object2IntOpenHashMap<String> nodeKey = getNodeKey(label);
        int node2 = nodeKey.getInt(to);
        return (List<Integer>)related.get(type).getNodesByValue(node2);
    }

    public Iterator<Map<String, Object>> getAllNodes() {
        return nodes.iterator();
    }

    public Iterator<Map<String,Object>> getNodes(String label) {
        if(nodeKeys.containsKey(label)) {
            Iterator<Integer> iter = nodeKeys.get(label).values().iterator();
            return new NodeIterator(iter).invoke();
        }

        return null;
    }

    public Iterator<Map<String, Object>> getAllRelationships() {
        return relationships.iterator();
    }

    public Iterator<Map<String, Object>> getRelationships(String type) {
        if (related.containsKey(type)) {
            Iterator<Integer> iter = related.get(type).getAllRels().iterator();
            return new RelationshipIterator(iter).invoke();
        }
        return null;
    }

    public boolean related(String label1, String from, String label2, String to) {
        return related(label1, from, label2, to, "all", new ArrayList<>());
    }

    public boolean related(String label1, String from, String label2, String to, String direction, String type) {
        return related(label1, from, label2, to, direction, new ArrayList<String>(){{ add(type); }});
    }
    public boolean related(String label1, String from, String label2, String to, String direction, List<String> types) {
        Object2IntOpenHashMap<String> node1Key = getNodeKey(label1);
        Object2IntOpenHashMap<String> node2Key = getNodeKey(label2);

        int node1 = node1Key.getInt(from);
        int node2 = node2Key.getInt(to);

        return related(node1, node2, direction, types);
    }

    public boolean related(int node1, int node2, String direction, List<String> types) {
        if (node1 == -1 || node2 == -1) { return false; }
        List<String> relTypes;
        if (types.isEmpty()) {
            relTypes = new ArrayList<>(related.keySet());
        } else {
            types.retainAll(related.keySet());
            relTypes = types;
        }

        for (String type : relTypes) {
            ReversibleMultiMap rels = related.get(type);
            if (direction.equals("all") || direction.equals("out")) {
                if (rels.containsPair(node1, node2)) {
                    return true;
                }
            }
            if (direction.equals("all") || direction.equals("in")) {
                if (rels.containsPair(node2, node1)) {
                    return true;
                }
            }
        }

        return false;
    }


    private class RelationshipIterator {
        private Iterator<Integer> iter;

        RelationshipIterator(Iterator<Integer> iter) {
            this.iter = iter;
        }

        Iterator<Map<String, Object>> invoke() {
            return new Iterator<Map<String, Object>>() {
                @Override
                public boolean hasNext() {
                    return iter.hasNext();
                }

                @Override
                public Map<String, Object> next() {
                    return relationships.get(iter.next());
                }
            };
        }
    }
    private class NodeIterator {
        private Iterator<Integer> iter;

        NodeIterator(Iterator<Integer> iter) {
            this.iter = iter;
        }

        Iterator<Map<String, Object>> invoke() {
            return new Iterator<Map<String, Object>>() {
                @Override
                public boolean hasNext() {
                    return iter.hasNext();
                }

                @Override
                public Map<String, Object> next() {
                    return nodes.get(iter.next());
                }
            };
        }
    }
}
