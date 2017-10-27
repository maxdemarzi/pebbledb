package com.pebbledb.graphs;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
// Adapted from http://stackoverflow.com/questions/23646186/a-java-multimap-which-allows-fast-lookup-of-key-by-value

public class ReversibleMultiMapLong {
    private Multimap<Integer, Long> key2Value = ArrayListMultimap.create();
    private Multimap<Integer, Long> value2key = ArrayListMultimap.create();

    private static Long getOtherValue(Integer key, Long value) {
        return ((long) value.intValue() << 32 | key & 0xFFFFFFFFL);
    }

    private static int getNode(long value) {
        return (int)value;
    }

    public static int getRel(long value) {
        return (int)(value >> 32);
    }

    public Collection<Long> getKeysByValue(Integer value) {
        return value2key.get(value);
    }

    public int size() {
        return key2Value.size();
    }

    public boolean isEmpty() {
        return key2Value.isEmpty();
    }

    public boolean containsNode(int node) {
        return key2Value.containsKey(node);
    }

    public boolean containsOtherNode(int node) {
        return value2key.containsKey(node);
    }

    public boolean containsEntry(int from, int to, int rel) {
        return key2Value.containsEntry(from, (long) rel << 32 | to & 0xFFFFFFFFL);
    }

    public boolean put(Integer from, Integer to, Integer rel) {
        value2key.put(to, (long) rel << 32 | from & 0xFFFFFFFFL);
        return key2Value.put(from, (long) rel << 32 | to & 0xFFFFFFFFL);
    }

    public boolean removeRelationship(Integer from, Integer to, Integer rel) {
        value2key.remove(to, (long) rel << 32 | from & 0xFFFFFFFFL);
        return key2Value.remove(from, (long) rel << 32 | to & 0xFFFFFFFFL);
    }

    public boolean putAll(Integer key, Iterable<? extends Long> values) {
        for (Long value : values) {
            value2key.put(getNode(value), getOtherValue(key, value));
        }
        return key2Value.putAll(key, values);
    }

    public Collection<Long> removeAll(int key) {
        Collection<Long> removed = key2Value.removeAll(key);
        for (Long value : removed) {
            value2key.remove(value, key);
        }
        for (Long reverse : value2key.removeAll(key)){
            key2Value.remove(reverse, key);
        }
        return removed;
    }

    public void clear() {
        value2key.clear();
        key2Value.clear();
    }

    public Collection<Long> get(Integer key) {
        return key2Value.get(key);
    }

    public Collection<Integer> getNodes(Integer key) {
        ArrayList<Integer> nodes = new ArrayList<>();
        for (long value : key2Value.get(key)) {
            nodes.add((int)value);
        }
        return nodes;
    }

    public Collection<Integer> getNodesByValue(Integer value) {
        ArrayList<Integer> nodes = new ArrayList<>();
        for (long key : value2key.get(value)) {
            nodes.add((int)key);
        }
        return nodes;
    }

    public Collection<Integer> getRels(Integer key) {
        ArrayList<Integer> rels = new ArrayList<>();
        for (long value : key2Value.get(key)) {
            rels.add((int)(value >> 32));
        }
        return rels;
    }

    public Collection<Integer> getRelsByValue(Integer value) {
        ArrayList<Integer> rels = new ArrayList<>();
        for (long key : value2key.get(value)) {
            rels.add((int)(key >> 32));
        }
        return rels;
    }

    public Set<Integer> keySet() {
        return key2Value.keySet();
    }

    public Multiset<Integer> keys() {
        return key2Value.keys();
    }

    public Collection<Long> values() {
        return key2Value.values();
    }

    public Collection<Map.Entry<Integer, Long>> entries() {
        return key2Value.entries();
    }

    public Map<Integer, Collection<Long>> asMap() {
        return key2Value.asMap();
    }

    public Collection<Integer> getAllRels() {
        ArrayList<Integer> rels = new ArrayList<>();
        for (long value : key2Value.values()) {
            rels.add((int)(value >> 32));
        }
        return rels;
    }

    public int getFromSize(Integer from) {
        return key2Value.get(from).size();
    }

    public int getToSize(Integer to) {
        return value2key.get(to).size();
    }
}
