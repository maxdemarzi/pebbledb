package com.pebbledb.graphs;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.*;

public class FastUtilGraph implements Graph {

    private Object2IntOpenHashMap<String> keys;
    private ObjectArrayList<Map<String, Object>> nodes;
    private Object2ObjectOpenHashMap<String, Map<String, Object>> relationships;
    private Object2ObjectOpenHashMap<String, ReversibleMultiMap<Integer>> related;
    private Object2IntArrayMap<String> relationshipCounts;
    private Object2IntOpenHashMap<String> relatedCounts;

    public FastUtilGraph() {
        keys = new Object2IntOpenHashMap<>();
        keys.defaultReturnValue(-1);
        nodes = new ObjectArrayList<>();
        relationships = new Object2ObjectOpenHashMap<>();
        related = new Object2ObjectOpenHashMap<>();
        relationshipCounts = new Object2IntArrayMap<>();
        relatedCounts = new Object2IntOpenHashMap<>();
    }

    public void clear() {
        keys.clear();
        nodes.clear();
        relationships.clear();
        related.clear();
        relationshipCounts.clear();
        relationshipCounts.defaultReturnValue(0);
        relatedCounts.clear();
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
    public boolean addNode (String key) {
        if (keys.containsKey(key)) {
            return false;
        } else {
            nodes.add(new HashMap<>());
            keys.put(key, nodes.size()-1);
        }
        return true;
    }

    public boolean addNode (String key, Map properties)  {
        if (keys.containsKey(key)) {
            return false;
        } else {
            nodes.add(properties);
            keys.put(key, nodes.size()-1);
            return true;
        }
    }

    public boolean removeNode(String key) {
        int id = keys.getInt(key);
        if(keys.containsKey(key)) {
            nodes.remove(id);
            for (String type : related.keySet()) {
                ReversibleMultiMap<Integer> rels = related.get(type);
                int outgoingCount = 0;
                int incomingCount = 0;
                for (Integer value : rels.get(id)) {
                    outgoingCount = relatedCounts.getInt(id + "-" + value + type);
                    if(outgoingCount > 1) {
                        for (int i = 2; i <= outgoingCount; i++) {
                            relationships.remove(id + "-" + value + type, i);
                        }
                    }
                    relationships.remove(id + "-" + value + type);
                }
                for (Integer value : rels.getKeysByValue(id)) {
                    incomingCount = relatedCounts.getInt(value + "-" + id + type);
                    if(incomingCount > 1) {
                        for (int i = 2; i <= incomingCount; i++) {
                            relationships.remove(value + "-" + id + type, i);
                        }
                    }
                    relationships.remove(value + "-" + id + type);
                }
                rels.removeAll(id);
                relationshipCounts.put(type, relationshipCounts.getInt(type) - (outgoingCount + incomingCount));
            }
            keys.removeInt(key);
            return true;
        } else {
            return false;
        }
    }

    public Map<String, Object> getNode(String key) {
        int id = keys.getInt(key);
        if (id == -1) { return null; }
        return nodes.get(keys.getInt(key));
    }

    public int getNodeId(String key) {
        return keys.getInt(key);
    }

    // Node Properties
    public Object getNodeProperty(String key, String property) {
        int id = keys.getInt(key);
        if (id == -1) { return null; }
        return nodes.get(id).get(property);
    }

    public boolean updateNodeProperties(String key, Map properties) {
        int id = keys.getInt(key);
        if (id == -1) { return false; }
        Map<String, Object> current = nodes.get(id);
        current.putAll(properties);
        return true;
    }

    public boolean deleteNodeProperties(String key) {
        int id = keys.getInt(key);
        if (id == -1) { return false; }
        nodes.add(id, new HashMap<>());
        return true;
    }

    public boolean updateNodeProperty(String key, String property, Object value) {
        int id = keys.getInt(key);
        if (id == -1) { return false; }
        Map<String, Object> properties = nodes.get(id);
        properties.put(property, value);
        return true;
    }

    public boolean deleteNodeProperty(String key, String property) {
        int id = keys.getInt(key);
        if (id == -1) { return false; }
        Map<String, Object> properties = nodes.get(id);
        properties.remove(property);
        return true;
    }

    // Relationships
    public boolean addRelationship(String type, String from, String to) {
        related.putIfAbsent(type, new ReversibleMultiMap<>());
        relationshipCounts.putIfAbsent(type, 0);
        relationshipCounts.put(type, relationshipCounts.getInt(type) + 1);

        int node1 = keys.getInt(from);
        int node2 = keys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        int count = relatedCounts.getInt(node1 + "-" + node2 + type);
        if (count == 0) {
            relatedCounts.put(node1 + "-" + node2 + type,1);
            related.get(type).put(node1, node2);
        } else {
            relatedCounts.put(node1 + "-" + node2 + type,count + 1);
            related.get(type).put(node1, node2);
        }
        return true;
    }

    public boolean addRelationship(String type, String from, String to, Map<String, Object> properties) {
        related.putIfAbsent(type, new ReversibleMultiMap<>());
        relationshipCounts.putIfAbsent(type, 0);
        relationshipCounts.put(type, relationshipCounts.getInt(type) + 1);

        int node1 = keys.getInt(from);
        int node2 = keys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        int count = relatedCounts.getInt(node1 + "-" + node2 + type);
        if ( count == 0) {
            relatedCounts.put(node1 + "-" + node2 + type,1);
            relationships.put(node1 + "-" + node2 + type, properties);
            related.get(type).put(node1, node2);
        } else {
            relationships.put(node1 + "-" + node2 + type + (count + 1), properties);
            relatedCounts.put(node1 + "-" + node2 + type,count + 1);
            related.get(type).put(node1, node2);
        }

        return true;
    }

    public boolean removeRelationship (String type, String from, String to) {
        if(!related.containsKey(type)) {
            return false;
        }
        int node1 = keys.getInt(from);
        int node2 = keys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }
        int count = relatedCounts.getInt(node1 + "-" + node2 + type);
        if ( count == 0) {
            return false;
        }
        related.get(type).remove(node1, node2);
        relationshipCounts.put(type, relationshipCounts.getInt(type) - 1);
        if ( count == 1) {
            relationships.remove(node1 + "-" + node2 + type);
        } else {
            relationships.remove(node1 + "-" + node2 + type, count);
        }

        return true;
    }
    
    public boolean removeRelationship(String type, String from, String to, int number) {
        if(!related.containsKey(type)) {
            return false;
        }
        int node1 = keys.getInt(from);
        int node2 = keys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }
        int count = relatedCounts.getInt(node1 + "-" + node2 + type);
        if ( count == 0 || count < number) {
            return false;
        }
        related.get(type).remove(node1, node2);
        relationshipCounts.put(type, relationshipCounts.getInt(type) - 1);
        if ( count == 1) {
            relationships.remove(node1 + "-" + node2 + type);
        } else {
            if (count != number) {
                relationships.put(node1 + "-" + node2 + type + number, relationships.get(node1 + "-" + node2 + type + count));
            }
            relationships.remove(node1 + "-" + node2 + type + count);
        }

        return true;
    }
    
    public Map<String, Object> getRelationship(String type, String from, String to) {
        int node1 = keys.getInt(from);
        int node2 = keys.getInt(to);
        Map<String, Object> rel = relationships.get(node1 + "-" + node2 + type);
        if (rel == null) {
            if (related.get(type).get(node1 ).contains(node2)) {
                return new HashMap<>();
            } else {
                return null;
            }
        }
        return rel;
    }
    
    public Map<String, Object> getRelationship(String type, String from, String to, int number) {
        int node1 = keys.getInt(from);
        int node2 = keys.getInt(to);

        Map<String, Object> rel = relationships.get(node1 + "-" + node2 + type + number);
        if (rel == null) {
            if (related.get(type).get(node1).contains(node2)) {
                return new HashMap<>();
            } else {
                return null;
            }
        }
        return rel;
    }

    // Relationship Properties
    
    public Object getRelationshipProperty(String type, String from, String to, String property) {
        int node1 = keys.getInt(from);
        int node2 = keys.getInt(to);
        if (node1 == -1 || node2 == -1) { return null; }

        return relationships.get(node1 + "-" + node2 + type).get(property);
    }

    public Object getRelationshipProperty(String type, String from, String to, int number, String property) {
        int node1 = keys.getInt(from);
        int node2 = keys.getInt(to);
        if (node1 == -1 || node2 == -1) { return null; }

        int count = relatedCounts.getInt(node1 + "-" + node2 + type);
        if ( count == 0 || count < number) {
            return null;
        }
        return relationships.get(node1 + "-" + node2 + type + number).get(property);
    }
    
    public boolean updateRelationshipProperties(String type, String from, String to, Map<String, Object> properties) {
        int node1 = keys.getInt(from);
        int node2 = keys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        relationships.put(node1 + "-" + node2 + type,properties);
        return true;
    }

    
    public boolean updateRelationshipProperties(String type, String from, String to, int number, Map<String, Object> properties) {
        int node1 = keys.getInt(from);
        int node2 = keys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        int count = relatedCounts.getInt(node1 + "-" + node2 + type);
        if ( count == 0 || count < number) {
            return false;
        }
        relationships.put(node1 + "-" + node2 + type + number, properties);
        return true;
    }
    
    public boolean deleteRelationshipProperties(String type, String from, String to) {
        int node1 = keys.getInt(from);
        int node2 = keys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }
        int count = relatedCounts.getInt(node1 + "-" + node2 + type);
        if ( count == 0 ) {
            return false;
        }
        relationships.remove(node1 + "-" + node2 + type);
        return true;
    }
    
    public boolean deleteRelationshipProperties(String type, String from, String to, int number) {
        int node1 = keys.getInt(from);
        int node2 = keys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        int count = relatedCounts.getInt(node1 + "-" + node2 + type);
        if ( count == 0 || count < number) {
            return false;
        }

        relationships.remove(node1 + "-" + node2 + type + count);
        return true;
    }

    public boolean updateRelationshipProperty(String type, String from, String to, String property, Object value) {
        int node1 = keys.getInt(from);
        int node2 = keys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }
        relationships.computeIfAbsent(node1 + "-" + node2 + type, k -> new HashMap<>());
        relationships.get(node1 + "-" + node2 + type).put(property, value);
        return true;
    }
    
    public boolean updateRelationshipProperty(String type, String from, String to, int number, String property, Object value) {
        int node1 = keys.getInt(from);
        int node2 = keys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        int count = relatedCounts.getInt(node1 + "-" + node2 + type);
        if ( count == 0 || count < number) {
            return false;
        }
        relationships.computeIfAbsent(node1 + "-" + node2 + type + number, k -> new HashMap<>());
        relationships.get(node1 + "-" + node2 + type + number).put(property, value);
        return true;
    }

    public boolean deleteRelationshipProperty(String type, String from, String to, String property) {
        int node1 = keys.getInt(from);
        int node2 = keys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        relationships.get(node1 + "-" + node2 + type).remove(property);
        return true;
    }
    
    public boolean deleteRelationshipProperty(String type, String from, String to, int number, String property) {
        int node1 = keys.getInt(from);
        int node2 = keys.getInt(to);
        if (node1 == -1 || node2 == -1) { return false; }

        int count = relatedCounts.getInt(node1 + "-" + node2 + type);
        if ( count == 0 || count < number) {
            return false;
        }
        relationships.get(node1 + "-" + node2 + type + number).remove(property);
        return true;
    }

    // Degrees
    public int getNodeDegree(String key) {
        return getNodeDegree(key, "all", new ArrayList<>());
    }

    public int getNodeDegree(String key, String direction) {
        return getNodeDegree(key, direction, new ArrayList<>());
    }

    public int getNodeDegree(String key, String direction, List<String> types) {
        int id = keys.getInt(key);
        if (id == -1) { return -1; }

        int count = 0;
        List<String> relTypes;
        if (types.size() == 0) {
            relTypes = new ArrayList<>(related.keySet());
        } else {
            types.retainAll(related.keySet());
            relTypes = types;
        }

        for (String type : relTypes) {
            ReversibleMultiMap<Integer> rels = related.get(type);
            if (direction.equals("all") || direction.equals("out")) {
                count += rels.get(id).size();
                //rels.get(id).stream().distinct().forEach(node2 -> count[0] += relatedCounts.getInt(id + "-" + node2 + type));
            }
            if (direction.equals("all") || direction.equals("in")) {
                //rels.getKeysByValue(id).stream().distinct().forEach(node2 -> count[0] += relatedCounts.getInt(node2 + "-" + id + type));
                count += rels.getKeysByValue(id).size();
            }
        }
        return count;
    }

    // Traversing
    public Object[] getOutgoingRelationshipNodes(String type, String from) {
        List<Integer> nodeIds = (List<Integer>)related.get(type).get(keys.getInt(from));
        int size = nodeIds.size();
        Object[] nodeArray = new Object[size];
        for(int i=-1; ++i < size;) {
            nodeArray[i] = nodes.get(nodeIds.get(i));
        }
        return nodeArray;
    }

    public Object[] getIncomingRelationshipNodes(String type, String to) {
        List<Integer> nodeIds = (List<Integer>)related.get(type).getKeysByValue(keys.getInt(to));
        int size = nodeIds.size();
        Object[] nodeArray = new Object[size];
        for(int i=-1; ++i < size;) {
            nodeArray[i] = nodes.get(nodeIds.get(i));
        }
        return nodeArray;
    }

    public Object[] getOutgoingRelationshipNodes(String type, Integer from) {
        List<Integer> nodeIds = (List<Integer>)related.get(type).get(from);
        int size = nodeIds.size();
        Object[] nodeArray = new Object[size];
        for(int i=-1; ++i < size;) {
            nodeArray[i] = nodes.get(nodeIds.get(i));
        }
        return nodeArray;
    }

    public Object[] getIncomingRelationshipNodes(String type, Integer to) {
        List<Integer> nodeIds = (List<Integer>)related.get(type).getKeysByValue(to);
        int size = nodeIds.size();
        Object[] nodeArray = new Object[size];
        for(int i=-1; ++i < size;) {
            nodeArray[i] = nodes.get(nodeIds.get(i));
        }
        return nodeArray;
    }

    public List<Integer> getOutgoingRelationshipNodeIds(String type, Integer from) {
        return (List<Integer>)related.get(type).get(from);
    }

    public List<Integer> getOutgoingRelationshipNodeIds(String type, String from) {
        return (List<Integer>)related.get(type).get(keys.getInt(from));
    }

    public List<Integer> getIncomingRelationshipNodeIds(String type, Integer to) {
        return (List<Integer>)related.get(type).getKeysByValue(to);
    }

    public List<Integer> getIncomingRelationshipNodeIds(String type, String to) {
        return (List<Integer>)related.get(type).getKeysByValue(keys.getInt(to));
    }

}
