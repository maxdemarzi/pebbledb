package com.pebbledb.graphs;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

// Adapted from http://stackoverflow.com/questions/23646186/a-java-multimap-which-allows-fast-lookup-of-key-by-value

public class ReversibleMultiMap implements Multimap<Integer, Long> {

    private Multimap<Integer, Long> key2Value = ArrayListMultimap.create();
    private Multimap<Integer, Long> value2key = ArrayListMultimap.create();

    private static Long getOtherValue(Integer key, Long value) {
        return ((long) key << 32 | (value.intValue()) & 0xFFFFFFFFL);
    }

    public static int getNode(long value) {
        return (int)(value >> 32);
    }

    private static int getNode(Object value) {
        return (int)((long)value >> 32);
    }

    public static int getRel(long value) {
        return (int)value;
    }

    public Collection<Long> getKeysByValue(Integer value) {
        return value2key.get(value);
    }

    @Override
    public int size() {
        return key2Value.size();
    }

    @Override
    public boolean isEmpty() {
        return key2Value.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return key2Value.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return key2Value.containsValue(value);
    }

    @Override
    public boolean containsEntry(Object key, Object value) {
        return key2Value.containsEntry(key, value);
    }

    @Override
    public boolean put(Integer key, Long value) {
        value2key.put(getNode(value), getOtherValue(key, value));
        return key2Value.put(key, value);
    }

    public boolean put(Integer from, Integer to, Integer rel) {
        Long otherValue = to.longValue();
        otherValue = otherValue << 32;
        otherValue += rel;

        Long value = from.longValue();
        value = value << 32;
        value += rel;

        value2key.put(to, value);
        return key2Value.put(from, otherValue);
    }

    @Override
    public boolean remove(Object key, Object value) {
        value2key.remove(getNode((long)value), getOtherValue((int)key, (long)value));
        return key2Value.remove(key, value);
    }

    public boolean removeRelationship(Integer from, Integer to, Integer id) {
        Long otherValue = to.longValue();
        otherValue = otherValue << 32;
        otherValue += id;

        Long value = from.longValue();
        value = value << 32;
        value += id;


        value2key.remove(to, value);
        return key2Value.remove(from, otherValue);
    }

    @Override
    public boolean putAll(Integer key, Iterable<? extends Long> values) {
        for (Long value : values) {
            value2key.put(getNode(value), getOtherValue(key, value));
        }
        return key2Value.putAll(key, values);
    }

    @Override
    public boolean putAll(Multimap<? extends Integer, ? extends Long> multimap) {
        for (Entry<? extends Integer, ? extends Long> e : multimap.entries()) {

            value2key.put(getNode(e.getValue()), getOtherValue(e.getKey(), e.getValue()));
        }
        return key2Value.putAll(multimap);
    }

    @Override
    public Collection<Long> replaceValues(Integer key, Iterable<? extends Long> values) {
        Collection<Long> replaced = key2Value.replaceValues(key, values);
        for (Long value : replaced) {
            value2key.remove(getNode(value), key);
        }
        for (Long value : values) {
            value2key.put(getNode(value), getOtherValue(key, value));
        }
        return replaced;
    }

    @Override
    public Collection<Long> removeAll(Object key) {
        Collection<Long> removed = key2Value.removeAll(key);
        for (Long value : removed) {
            value2key.remove(value, key);
        }
        for (Long reverse : value2key.removeAll(key)){
            key2Value.remove(reverse, key);
        }
        return removed;
    }

    @Override
    public void clear() {
        value2key.clear();
        key2Value.clear();
    }

    @Override
    public Collection<Long> get(Integer key) {
        return key2Value.get(key);
    }

    public Collection<Integer> getNodes(Integer key) {
        ArrayList<Integer> nodes = new ArrayList<>();
        key2Value.get(key).stream().map(ReversibleMultiMap::getNode).forEach(nodes::add);
        return nodes;
    }

    public Collection<Integer> getNodesByValue(Integer value) {
        ArrayList<Integer> nodes = new ArrayList<>();
        value2key.get(value).stream().map(ReversibleMultiMap::getNode).forEach(nodes::add);
        return nodes;
    }

    public Collection<Integer> getRels(Integer key) {
        ArrayList<Integer> rels = new ArrayList<>();
        key2Value.get(key).stream().map(ReversibleMultiMap::getRel).forEach(rels::add);
        return rels;
    }


    public Collection<Integer> getRelsByValue(Integer value) {
        ArrayList<Integer> rels = new ArrayList<>();
        value2key.get(value).stream().map(ReversibleMultiMap::getRel).forEach(rels::add);
        return rels;
    }

    @Override
    public Set<Integer> keySet() {
        return key2Value.keySet();
    }

    @Override
    public Multiset<Integer> keys() {
        return key2Value.keys();
    }

    @Override
    public Collection<Long> values() {
        return key2Value.values();
    }

    @Override
    public Collection<Entry<Integer, Long>> entries() {
        return key2Value.entries();
    }

    @Override
    public Map<Integer, Collection<Long>> asMap() {
        return key2Value.asMap();
    }
}
