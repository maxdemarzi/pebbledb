package com.pebbledb.graphs;

import com.pebbledb.Graph;
import com.pebbledb.ReversibleMultiMap;
import it.unimi.dsi.fastutil.objects.*;

import java.util.*;
import java.util.stream.Stream;

public class FastUtilGraph implements Graph {

    private static Object2IntOpenHashMap<String> keys;
    private static ArrayList<Map<String, Object>> nodes;
    private static Object2ObjectOpenHashMap<String, Map<String, Object>> relationships;
    private static Object2ObjectOpenHashMap<String, ReversibleMultiMap<Integer>> related;
    private static Object2IntArrayMap<String> relationshipCounts;
    private static Object2IntOpenHashMap<String> relatedCounts;

    public FastUtilGraph() {
        keys = new Object2IntOpenHashMap<>();
        keys.defaultReturnValue(-1);
        nodes = new ArrayList<>();
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
        nodes.add(id, properties);
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

        int count = relatedCounts.getInt(node1 + "-" + node2 + type);
        if (count == 0) {
            relatedCounts.put(node1 + "-" + node2 + type,1);
            related.get(type).put(keys.getInt(from), keys.getInt(to));
        } else {
            relatedCounts.put(node1 + "-" + node2 + type,count + 1);
        }
        return true;
    }

    public boolean addRelationship(String type, String from, String to, Map<String, Object> properties) {
        related.putIfAbsent(type, new ReversibleMultiMap<>());
        relationshipCounts.putIfAbsent(type, 0);
        relationshipCounts.put(type, relationshipCounts.getInt(type) + 1);

        int count = relatedCounts.getInt(from + "-" + to + type);
        if ( count == 0) {
            relatedCounts.put(from + "-" + to + type,1);
            relationships.put(from + "-" + to + type, properties);
            related.get(type).put(keys.getInt(from), keys.getInt(to));
        } else {
            relationships.put(from + "-" + to + type + (count + 1), properties);
            relatedCounts.put(from + "-" + to + type,count + 1);
        }

        return true;
    }

    public Map<String, Object> getRelationship(String type, String from, String to) {
        Map<String, Object> rel = relationships.get(from + "-" + to + type);
        if (rel == null) {
            if (related.get(type).get(keys.getInt(from)).contains(keys.getInt(to))) {
                return new HashMap<>();
            } else {
                return null;
            }
        }
        return rel;
    }

    @Override
    public Map<String, Object> getRelationship(String type, String from, String to, int number) {
        return null;
    }

    @Override
    public Object getRelationshipProperty(String type, String from, String to, String property) {
        return null;
    }

    @Override
    public Object getRelationshipProperty(String type, String from, String to, int number, String property) {
        return null;
    }

    @Override
    public boolean updateRelationshipProperties(String type, String from, String to, Map<String, Object> properties) {
        return false;
    }

    @Override
    public boolean updateRelationshipProperties(String type, String from, String to, int number, Map<String, Object> properties) {
        return false;
    }

    public Map<String, Object> getRelationshipTwo(String type, String from, String to) {
            Map<String, Object> rel = relationships.get(from + "-" + to + type);
        if (rel == null) {
            if (related.get(type).get(keys.getInt(from)).contains(keys.getInt(to))) {
                return new HashMap<>();
            } else {
                return null;
            }
        }
        return rel;
    }

    public Map<String, Object> getRelationshipTwo(String type, String from, String to, Long number) {
        Map<String, Object> rel = relationships.get(from + "-" + to + type + number);
        if (rel == null) {
            if (relatedCounts.getInt(from + "-" + to + type) >= number) {
                return new HashMap<>();
            } else {
                return null;
            }
        }
        return rel;
    }

    // TODO: 7/30/17 Continue adding capability of having multiple same type relationships between nodes 
    public boolean deleteRelationshipProperties(String type, String from, String to) {
        Map<String, Object> rel = relationships.get(from + "-" + to + type);
        if (rel == null) {
            if (!related.get(type).get(keys.getInt(from)).contains(to)) {
                return false;
            }
        } else {
            relationships.remove(from + "-" + to + type);
        }
        return true;
    }

    @Override
    public boolean deleteRelationshipProperties(String type, String from, String to, int number) {
        return false;
    }

    @Override
    public boolean updateRelationshipProperty(String type, String from, String to, String property, Object value) {
        return false;
    }

    @Override
    public boolean updateRelationshipProperty(String type, String from, String to, int number, String property, Object value) {
        return false;
    }

    @Override
    public boolean deleteRelationshipProperty(String type, String from, String to, String property) {
        return false;
    }

    @Override
    public boolean deleteRelationshipProperty(String type, String from, String to, int number, String property) {
        return false;
    }

    public boolean removeRelationship (String type, String from, String to) {
        if(!related.containsKey(type)) {
            return false;
        }

        if (related.get(type).remove(keys.getInt(from), keys.getInt(to))) {
            relationships.remove(keys.getInt(from) + "-" + keys.getInt(to) + type);
            relationshipCounts.put(type, relationshipCounts.getInt(type) - 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeRelationship(String type, String from, String to, int number) {
        //throw new java.lang.UnsupportedOperationException("Not supported yet.");
        return false;
    }

    public Object[] getOutgoingRelationshipNodeIds(String type, String from) {
        return related.get(type).get(keys.getInt(from)).toArray();
    }

    public Object[] getIncomingRelationshipNodeIds(String type, String from) {
        return related.get(type).getKeysByValue(keys.getInt(from)).toArray();
    }

    public Object[] getOutgoingRelationshipNodes(String type, String from) {
        Object[] nodeIds = related.get(type).get(keys.getInt(from)).toArray();
        for(int i = 0; i < nodeIds.length; i++) {
            nodeIds[i] = nodes.get((int)nodeIds[i]);
        }
        return nodeIds;
    }

    public Stream<Object> getOutgoingRelationshipNodesTwo(String type, String from) {
        return related.get(type).get(keys.getInt(from)).stream().map(i -> nodes.get(i));
    }

    public Object[] getIncomingRelationshipNodes(String type, String from) {
        Object[] nodeIds = related.get(type).getKeysByValue(keys.getInt(from)).toArray();
        for(int i = 0; i < nodeIds.length; i++) {
            nodeIds[i] = nodes.get((int)nodeIds[i]);
        }
        return nodeIds;
    }
}
