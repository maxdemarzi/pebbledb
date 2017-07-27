package com.pebbledb;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.*;
import java.util.stream.Collectors;

public class Graph {

    private static Object2IntOpenHashMap<String> keys;
    private static ArrayList<HashMap<String, Object>> nodes;
    // At some point Benchmark against this:
    //private static BigList<HashMap<String, Object>> nodes;
    private static Object2ObjectOpenHashMap<String, HashMap<String, Object>> relationships;
    private static Object2ObjectOpenHashMap<String, ReversibleMultiMap<Integer>> related;

    public Graph() {
        keys = new Object2IntOpenHashMap<>();
        keys.defaultReturnValue(-1);
        nodes = new ArrayList<>();
        relationships = new Object2ObjectOpenHashMap<>();
        related = new Object2ObjectOpenHashMap<>();
    }

    public void clear() {
        keys.clear();
        nodes.clear();
        relationships.clear();
        related.clear();
    }

    public HashMap<String, Object> getRelationshipTypeCounts(String type) {
        HashMap<String, Object> counts = new HashMap<>();
        if (related.containsKey(type)) {
            counts.put(type, related.get(type).size());
        }
        return counts;
    }

    public List<String> getRelationshipTypes() {
        return new ArrayList<>(related.keySet());
    }

    public boolean addNode (String key) {
        if (keys.containsKey(key)) {
            return false;
        } else {
            nodes.add(new HashMap<>());
            keys.put(key, nodes.size()-1);
        }
        return true;
    }

    public boolean addNode (String key, HashMap properties)  {
        if (keys.containsKey(key)) {
            return false;
        } else {
            nodes.add(properties);
            keys.put(key, nodes.size()-1);
            return true;
        }
    }

    public HashMap<String, Object> getNode(String key) {
        int id = keys.getInt(key);
        if (id == -1) { return null; }
        return nodes.get(keys.getInt(key));
    }

    public int getNodeId(String key) {
        return keys.getInt(key);
    }

    public boolean removeNode(String key) {
        int id = keys.getInt(key);
        if(keys.containsKey(key)) {
            nodes.remove(id);
            for (String type : related.keySet()) {
                ReversibleMultiMap<Integer> rels = related.get(type);
                for (Integer value : rels.get(id)) {
                    relationships.remove(id + "-" + value + type);
                }
                for (Integer value : rels.getKeysByValue(id)) {
                    relationships.remove(value + "-" + id + type);
                }
                rels.removeAll(id);
            }
            keys.removeInt(key);
            return true;
        } else {
            return false;
        }
    }

    public boolean addRelationship (String type, String from, String to) {
        related.putIfAbsent(type, new ReversibleMultiMap<>());

        return related.get(type).put(keys.getInt(from), keys.getInt(to));
    }

    public boolean addRelationship (String type, String from, String to, HashMap properties) {
        relationships.put(from + "-" + to + type, properties);
        related.putIfAbsent(type, new ReversibleMultiMap<>());
        return related.get(type).put(keys.getInt(from), keys.getInt(to));
    }

    public HashMap<String, Object> getRelationship(String type, String from, String to) {
        HashMap<String, Object> rel = relationships.get(from + "-" + to + type);
        if (rel == null) {
            if (related.get(type).get(keys.getInt(from)).contains(keys.getInt(to))) {
                return new HashMap<>();
            } else {
                return null;
            }
        }
        return rel;
    }

    public boolean deleteRelationshipProperties(String type, String from, String to) {
        HashMap<String, Object> rel = relationships.get(from + "-" + to + type);
        if (rel == null) {
            if (!related.get(type).get(keys.getInt(from)).contains(to)) {
                return false;
            }
        } else {
            relationships.remove(from + "-" + to + type);
        }
        return true;
    }

    public boolean removeRelationship (String type, String from, String to) {
        if(!related.containsKey(type)) {
            return false;
        }
        related.get(type).remove(keys.getInt(from), keys.getInt(to));
        relationships.remove(keys.getInt(from) + "-" + keys.getInt(to) + type);
        return true;
    }

    public Collection<Integer> getOutgoingRelationshipNodeIds(String type, String from) {
        return related.get(type).get(keys.getInt(from));
    }

    public HashSet<Integer> getIncomingRelationshipNodeIds(String type, String to) {
        return new HashSet<>(related.get(type).getKeysByValue(keys.getInt(to)));
    }

    public Collection<HashMap<String, Object>> getOutgoingRelationshipNodes(String type, String from) {

        int[] nodeIds = related.get(type).get(keys.getInt(from)).stream().mapToInt(i->i).toArray();
        Collection<HashMap<String, Object>> results = new ArrayList<>(nodeIds.length);
        for (int key : nodeIds) {
            results.add(nodes.get(key));
        }
        return results;
    }

    public Collection<HashMap<String, Object>> getOutgoingRelationshipNodesTwo(String type, String from) {
        Collection<HashMap<String, Object>> results = new ArrayList<>(110);
        for (int key : related.get(type).get(keys.getInt(from)) ) {
            results.add(nodes.get(key));
        }
        return results;
    }

    public Collection<HashMap<String, Object>> getOutgoingRelationshipNodesThree(String type, String from) {
        return related.get(type).get(keys.getInt(from)).stream().map(nodes::get).collect(Collectors.toCollection(ArrayList::new));
    }

    public Set<Object> getIncomingRelationshipNodes(String type, String from) {
        Set<Object> results = new HashSet<>();
        for (Integer key : related.get(type).getKeysByValue(keys.getInt(from)) ) {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("properties", nodes.get(key));
            results.add(properties);
        }
        return results;
    }
}
