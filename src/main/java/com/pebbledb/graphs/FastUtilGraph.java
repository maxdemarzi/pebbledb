package com.pebbledb.graphs;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.roaringbitmap.RoaringBitmap;

import java.util.*;

public class FastUtilGraph implements Graph {

    private Object2IntOpenHashMap<String> nodeKeys;
    private ObjectArrayList<Map<String, Object>> nodes;
    private Object2IntOpenHashMap<String> relationshipKeys;
    private ObjectArrayList<Map<String, Object>> relationships;
    private Object2ObjectOpenHashMap<String, ReversibleMultiMap> related;
    private Object2IntArrayMap<String> relationshipCounts;
    private Object2IntOpenHashMap<String> relatedCounts;
    private RoaringBitmap deletedNodes;
    private RoaringBitmap deletedRelationships;
    private Object2ObjectOpenHashMap<String, RoaringBitmap> labels;

    public FastUtilGraph() {
        nodeKeys = new Object2IntOpenHashMap<>();
        nodeKeys.defaultReturnValue(-1);
        nodes = new ObjectArrayList<>();
        relationshipKeys = new Object2IntOpenHashMap<>();
        relationshipKeys.defaultReturnValue(-1);
        relationships = new ObjectArrayList<>();
        related = new Object2ObjectOpenHashMap<>();
        relationshipCounts = new Object2IntArrayMap<>();
        relatedCounts = new Object2IntOpenHashMap<>();
        deletedNodes = new RoaringBitmap();
        deletedRelationships = new RoaringBitmap();
        labels = new Object2ObjectOpenHashMap<>();
    }

    private static String generateKey(String label, String identifier) {
        return label + "-" + identifier;
    }

    public void clear() {
        nodeKeys.clear();
        nodes.clear();
        relationships.clear();
        related.clear();
        relationshipCounts.clear();
        relationshipCounts.defaultReturnValue(0);
        relatedCounts.clear();
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

    // Nodes
    public boolean addNode (String label, String identifier) {
        String key = generateKey(label, identifier);
        if (nodeKeys.containsKey(key)) {
            return false;
        } else {
            int nodeId;
            if (deletedNodes.isEmpty()) {
                nodes.add(new HashMap<>());
                nodeId = nodes.size() - 1;
                nodeKeys.put(key, nodeId);
            } else {
                nodeId = deletedNodes.first();
                nodes.set(nodeId, new HashMap<>());
                nodeKeys.put(key, nodeId);
                deletedNodes.remove(nodeId);
            }
            if (!labels.containsKey(label)) {
                labels.put(label, new RoaringBitmap());
            }
            labels.get(label).add(nodeId);
        }
        return true;
    }

    public boolean addNode (String label, String identifier, Map<String, Object> properties) {
        String key = generateKey(label, identifier);
        if (nodeKeys.containsKey(key)) {
            return false;
        } else {
            int nodeId;
            if (deletedNodes.isEmpty()) {
                nodes.add(properties);
                nodeId = nodes.size() -1;
                nodeKeys.put(key, nodeId);
            } else {
                nodeId = deletedNodes.first();
                nodes.set(nodeId, properties);
                nodeKeys.put(key, nodeId);
                deletedNodes.remove(nodeId);
            }
            if (!labels.containsKey(label)) {
                labels.put(label, new RoaringBitmap());
            }
            labels.get(label).add(nodeId);

            return true;
        }
    }

    public boolean removeNode(String label, String identifier) {
        String key = generateKey(label, identifier);
        if(!nodeKeys.containsKey(key)) {
            return false;
        }

        int id = nodeKeys.getInt(key);
        nodes.set(id, null);
        deletedNodes.add(id);
        labels.get(label).remove(id);

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
        nodeKeys.removeInt(key);

        return true;
    }

    public Map<String, Object> getNode(String label, String identifier) {
        String key = generateKey(label, identifier);
        int id = nodeKeys.getInt(key);
        if (id == -1) { return null; }
        return nodes.get(nodeKeys.getInt(key));
    }

    public int getNodeId(String label, String identifier) {
        String key = generateKey(label, identifier);
        return nodeKeys.getInt(key);
    }

    // Node Properties
    public Object getNodeProperty(String label, String identifier, String property) {
        String key = generateKey(label, identifier);
        int id = nodeKeys.getInt(key);
        if (id == -1) { return null; }
        return nodes.get(id).get(property);
    }

    public boolean updateNodeProperties(String label, String identifier, Map<String, Object> properties) {
        String key = generateKey(label, identifier);
        int id = nodeKeys.getInt(key);
        if (id == -1) { return false; }
        Map<String, Object> current = nodes.get(id);
        current.putAll(properties);
        return true;
    }

    public boolean deleteNodeProperties(String label, String identifier) {
        String key = generateKey(label, identifier);
        int id = nodeKeys.getInt(key);
        if (id == -1) { return false; }
        nodes.add(id, new HashMap<>());
        return true;
    }

    public boolean updateNodeProperty(String label, String identifier, String property, Object value) {
        String key = generateKey(label, identifier);
        int id = nodeKeys.getInt(key);
        if (id == -1) { return false; }
        Map<String, Object> properties = nodes.get(id);
        properties.put(property, value);
        return true;
    }

    public boolean deleteNodeProperty(String label, String identifier, String property) {
        String key = generateKey(label, identifier);
        int id = nodeKeys.getInt(key);
        if (id == -1) { return false; }
        Map<String, Object> properties = nodes.get(id);
        properties.remove(property);
        return true;
    }

    // Relationships
    public boolean addRelationship(String type, String label1, String identifier1, String label2, String identifier2) {
        String from = generateKey(label1, identifier1);
        String to = generateKey(label2, identifier2);

        related.putIfAbsent(type, new ReversibleMultiMap());
        relationshipCounts.putIfAbsent(type, 0);
        relationshipCounts.put(type, relationshipCounts.getInt(type) + 1);

        int node1 = nodeKeys.getInt(from);
        int node2 = nodeKeys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        int count = relatedCounts.getInt(node1 + "-" + node2 + "-" + type) + 1;
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("_incoming_node_id", node1);
        properties.put("_outgoing_node_id", node2);

        relationships.add(properties);
        relatedCounts.put(node1 + "-" + node2 + "-" + type, count);
        related.get(type).put(node1, node2, relationships.size() -1);
        relationshipKeys.put(node1 + "-" + node2 + "-" + type + "-" + count, relationships.size() - 1);

        return true;
    }

    public boolean addRelationship(String type, String label1, String identifier1, String label2, String identifier2, Map<String, Object> properties) {
        String from = generateKey(label1, identifier1);
        String to = generateKey(label2, identifier2);

        related.putIfAbsent(type, new ReversibleMultiMap());
        relationshipCounts.putIfAbsent(type, 0);
        relationshipCounts.put(type, relationshipCounts.getInt(type) + 1);

        int node1 = nodeKeys.getInt(from);
        int node2 = nodeKeys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }
        properties.put("_incoming_node_id", node1);
        properties.put("_outgoing_node_id", node2);

        int count = relatedCounts.getInt(node1 + "-" + node2 + "-" + type) + 1;
        relationships.add(properties);
        relatedCounts.put(node1 + "-" + node2 + "-" + type, count);
        related.get(type).put(node1, node2, relationships.size() - 1);
        relationshipKeys.put(node1 + "-" + node2 + "-" + type + "-" + count, relationships.size() - 1);

        return true;
    }

    public boolean removeRelationship (String type, String label1, String identifier1, String label2, String identifier2) {
        String from = generateKey(label1, identifier1);
        String to = generateKey(label2, identifier2);

        if(!related.containsKey(type)) {
            return false;
        }
        int node1 = nodeKeys.getInt(from);
        int node2 = nodeKeys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }
        int count = relatedCounts.getInt(node1 + "-" + node2 + "-" + type);
        if (count == 0) {
            return false;
        }
        relatedCounts.put(node1 + "-" + node2 + "-" + type, count - 1);
        relationshipCounts.put(type, relationshipCounts.getInt(type) - 1);
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-" + count);
        related.get(type).removeRelationship(node1, node2, relId);
        relationships.set(relId, null);
        relationshipKeys.removeInt(node1 + "-" + node2 + "-" + type + "-" + count);

        return true;
    }

    public boolean removeRelationship(String type, String label1, String identifier1, String label2, String identifier2, int number) {
        String from = generateKey(label1, identifier1);
        String to = generateKey(label2, identifier2);

        if(!related.containsKey(type)) {
            return false;
        }
        int node1 = nodeKeys.getInt(from);
        int node2 = nodeKeys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }
        int count = relatedCounts.getInt(node1 + "-" + node2 + "-" + type);
        if (count == 0 || count < number) {
            return false;
        }
        relatedCounts.put(node1 + "-" + node2 + "-" + type, count - 1);
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

    public Map<String, Object> getRelationship(String type, String label1, String identifier1, String label2, String identifier2) {
        String from = generateKey(label1, identifier1);
        String to = generateKey(label2, identifier2);

        int node1 = nodeKeys.getInt(from);
        int node2 = nodeKeys.getInt(to);
        if (node1 == -1 || node2 == -1) { return null; }
        int count = relatedCounts.getInt(node1 + "-" + node2 + "-" + type);
        if (count == 0) { return null; }
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-" + 1);

        return relationships.get(relId);
    }

    public Map<String, Object> getRelationship(String type, String label1, String identifier1, String label2, String identifier2, int number) {
        String from = generateKey(label1, identifier1);
        String to = generateKey(label2, identifier2);

        int node1 = nodeKeys.getInt(from);
        int node2 = nodeKeys.getInt(to);
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-" + number);

        return relationships.get(relId);
    }

    // Relationship Properties

    public Object getRelationshipProperty(String type, String label1, String identifier1, String label2, String identifier2, String property) {
        String from = generateKey(label1, identifier1);
        String to = generateKey(label2, identifier2);

        int node1 = nodeKeys.getInt(from);
        int node2 = nodeKeys.getInt(to);
        if (node1 == -1 || node2 == -1) { return null; }

        int count = relatedCounts.getInt(node1 + "-" + node2 + "-" + type);
        if (count == 0) { return null; }
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-" + 1);
        return relationships.get(relId).get(property);
    }

    public Object getRelationshipProperty(String type, String label1, String identifier1, String label2, String identifier2, int number, String property) {
        String from = generateKey(label1, identifier1);
        String to = generateKey(label2, identifier2);

        int node1 = nodeKeys.getInt(from);
        int node2 = nodeKeys.getInt(to);
        if (node1 == -1 || node2 == -1) { return null; }

        int count = relatedCounts.getInt(node1 + "-" + node2 + "-" + type);
        if (count == 0 || count < number) { return null; }
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-" + number);
        return relationships.get(relId).get(property);
    }

    public boolean updateRelationshipProperties(String type, String label1, String identifier1, String label2, String identifier2, Map<String, Object> properties) {
        String from = generateKey(label1, identifier1);
        String to = generateKey(label2, identifier2);

        int node1 = nodeKeys.getInt(from);
        int node2 = nodeKeys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        int count = relatedCounts.getInt(node1 + "-" + node2 + "-" + type);
        if (count == 0) { return false; }
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-" + 1);
        relationships.set(relId, properties);
        return true;
    }


    public boolean updateRelationshipProperties(String type, String label1, String identifier1, String label2, String identifier2, int number, Map<String, Object> properties) {
        String from = generateKey(label1, identifier1);
        String to = generateKey(label2, identifier2);

        int node1 = nodeKeys.getInt(from);
        int node2 = nodeKeys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        int count = relatedCounts.getInt(node1 + "-" + node2 + "-" + type);
        if (count == 0 || count < number) { return false; }
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-" + number);
        relationships.set(relId, properties);
        return true;
    }

    public boolean deleteRelationshipProperties(String type, String label1, String identifier1, String label2, String identifier2) {
        String from = generateKey(label1, identifier1);
        String to = generateKey(label2, identifier2);

        int node1 = nodeKeys.getInt(from);
        int node2 = nodeKeys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        int count = relatedCounts.getInt(node1 + "-" + node2 + "-" + type);
        if (count == 0 ) { return false; }
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-" + 1);
        relationships.set(relId, new HashMap<>());
        return true;
    }

    public boolean deleteRelationshipProperties(String type, String label1, String identifier1, String label2, String identifier2, int number) {
        String from = generateKey(label1, identifier1);
        String to = generateKey(label2, identifier2);

        int node1 = nodeKeys.getInt(from);
        int node2 = nodeKeys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        int count = relatedCounts.getInt(node1 + "-" + node2 + "-" + type);
        if (count == 0 || count < number) { return false; }
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-" + number);
        relationships.set(relId, new HashMap<>());
        return true;
    }

    public boolean updateRelationshipProperty(String type, String label1, String identifier1, String label2, String identifier2, String property, Object value) {
        String from = generateKey(label1, identifier1);
        String to = generateKey(label2, identifier2);

        int node1 = nodeKeys.getInt(from);
        int node2 = nodeKeys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }
        int count = relatedCounts.getInt(node1 + "-" + node2 + "-" + type);
        if (count == 0) { return false; }
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-" + 1);
        relationships.get(relId).put(property, value);
        return true;
    }

    public boolean updateRelationshipProperty(String type, String label1, String identifier1, String label2, String identifier2, int number, String property, Object value) {
        String from = generateKey(label1, identifier1);
        String to = generateKey(label2, identifier2);

        int node1 = nodeKeys.getInt(from);
        int node2 = nodeKeys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        int count = relatedCounts.getInt(node1 + "-" + node2 + "-" + type);
        if (count == 0 || count < number) { return false; }
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-" + number);
        relationships.get(relId).put(property, value);
        return true;
    }

    public boolean deleteRelationshipProperty(String type, String label1, String identifier1, String label2, String identifier2, String property) {
        String from = generateKey(label1, identifier1);
        String to = generateKey(label2, identifier2);

        int node1 = nodeKeys.getInt(from);
        int node2 = nodeKeys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        int count = relatedCounts.getInt(node1 + "-" + node2 + "-" + type);
        if (count == 0) { return false; }
        int relId = relationshipKeys.getInt(node1 + "-" + node2 + "-" + type + "-" + 1);

        Map<String, Object> properties = relationships.get(relId);
        if (properties.containsKey(property)) {
            properties.remove(property);
            return true;
        }
        return false;
    }

    public boolean deleteRelationshipProperty(String type, String label1, String identifier1, String label2, String identifier2, int number, String property) {
        String from = generateKey(label1, identifier1);
        String to = generateKey(label2, identifier2);

        int node1 = nodeKeys.getInt(from);
        int node2 = nodeKeys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        int count = relatedCounts.getInt(node1 + "-" + node2 + "-" + type);
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

    public int getNodeDegree(String label, String identifier, String direction, List<String> types) {
        String key = generateKey(label, identifier);
        int id = nodeKeys.getInt(key);
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
    public List<Map<String, Object>> getOutgoingRelationships(String label, String identifier) {
        String from = generateKey(label, identifier);
        int node1 = nodeKeys.getInt(from);
        List<Map<String,Object>> nodeRelationships = new ArrayList<>();
        for (String type : related.keySet()) {
            for (Integer rel : related.get(type).getRels(node1)) {
                nodeRelationships.add(relationships.get(rel));
            }
        }
        return nodeRelationships;
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

    public List<Map<String,Object>> getOutgoingRelationships(String type, String label, String identifier) {
        String from = generateKey(label, identifier);
        int node1 = nodeKeys.getInt(from);

        List<Map<String,Object>> nodeRelationships = new ArrayList<>();
        for (Integer rel : related.get(type).getRels(node1)) {
            nodeRelationships.add(relationships.get(rel));
        }
        return nodeRelationships;
    }

    public List<Map<String,Object>> getOutgoingRelationships(String type, int node) {
        List<Map<String,Object>> nodeRelationships = new ArrayList<>();
        for (Integer rel : related.get(type).getRels(node)) {
            nodeRelationships.add(relationships.get(rel));
        }
        return nodeRelationships;
    }

    public List<Map<String,Object>> getIncomingRelationships(String label, String identifier) {
        String to = generateKey(label, identifier);
        int node2 = nodeKeys.getInt(to);

        List<Map<String,Object>> nodeRelationships = new ArrayList<>();
        for (String type : related.keySet()) {
            for (Integer rel : related.get(type).getRelsByValue(node2)) {
                nodeRelationships.add(relationships.get(rel));
            }
        }
        return nodeRelationships;
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

    public List<Map<String,Object>> getIncomingRelationships(String type, String label, String identifier) {
        String to = generateKey(label, identifier);
        int node2 = nodeKeys.getInt(to);

        List<Map<String,Object>> nodeRelationships = new ArrayList<>();
        for (Integer rel : related.get(type).getRelsByValue(node2)) {
            nodeRelationships.add(relationships.get(rel));
        }
        return nodeRelationships;
    }

    public List<Map<String,Object>> getIncomingRelationships(String type, int node) {
        List<Map<String,Object>> nodeRelationships = new ArrayList<>();
        for (Integer rel : related.get(type).getRelsByValue(node)) {
            nodeRelationships.add(relationships.get(rel));
        }
        return nodeRelationships;
    }

    public Object[] getOutgoingRelationshipNodes(String type, String label, String identifier) {
        String from = generateKey(label, identifier);

        List<Integer> nodeIds = (List<Integer>)related.get(type).getNodes(nodeKeys.getInt(from));
        int size = nodeIds.size();
        Object[] nodeArray = new Object[size];
        for(int i=-1; ++i < size;) {
            nodeArray[i] = nodes.get(nodeIds.get(i));
        }
        return nodeArray;
    }

    public Object[] getIncomingRelationshipNodes(String type, String label, String identifier) {
        String to = generateKey(label, identifier);
        List<Integer> nodeIds = (List<Integer>)related.get(type).getNodesByValue(nodeKeys.getInt(to));
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

    public List<Integer> getOutgoingRelationshipNodeIds(String type, String label, String identifier) {
        String from = generateKey(label, identifier);
        return (List<Integer>)related.get(type).getNodes(nodeKeys.getInt(from));
    }

    public List<Integer> getIncomingRelationshipNodeIds(String type, Integer to) {
        return (List<Integer>)related.get(type).getNodesByValue(to);
    }

    public List<Integer> getIncomingRelationshipNodeIds(String type, String label, String identifier) {
        String to = generateKey(label, identifier);
        return (List<Integer>)related.get(type).getNodesByValue(nodeKeys.getInt(to));
    }

    public Iterator<Map<String, Object>> getAllNodes() {
        return nodes.iterator();
    }

    public Iterator<Map<String,Object>> getNodes(String label) {
        if (labels.containsKey(label)) {
            Iterator<Integer> iter = labels.get(label).iterator();
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

    public boolean related(String label1, String identifier1, String label2, String identifier2) {
        return related(label1, identifier1, label2, identifier2, "all", new ArrayList<>());
    }

    public boolean related(String label1, String identifier1, String label2, String identifier2, String direction, String type) {
        return related(label1, identifier1, label2, identifier2, direction, new ArrayList<String>(){{ add(type); }});
    }
    public boolean related(String label1, String identifier1, String label2, String identifier2, String direction, List<String> types) {
        String from = generateKey(label1, identifier1);
        String to = generateKey(label2, identifier2);
        return related(nodeKeys.getInt(from), nodeKeys.getInt(to), direction, types);
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
