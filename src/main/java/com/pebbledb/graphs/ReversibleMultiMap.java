package com.pebbledb.graphs;

import com.google.common.collect.*;

import java.util.*;
import java.util.Map.Entry;

// Adapted from http://stackoverflow.com/questions/23646186/a-java-multimap-which-allows-fast-lookup-of-key-by-value

public class ReversibleMultiMap<Integer> implements Multimap<Integer, Integer> {

        private Multimap<Integer, Integer> key2Value = ArrayListMultimap.create();
        private Multimap<Integer, Integer> value2key = ArrayListMultimap.create();

        public Collection<Integer> getKeysByValue(Integer value) {
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
        public boolean put(Integer key, Integer value) {
            value2key.put(value, key);
            return key2Value.put(key, value);
        }

        @Override
        public boolean remove(Object key, Object value) {
            value2key.remove(value, key);
            return key2Value.remove(key, value);
        }

        @Override
        public boolean putAll(Integer key, Iterable<? extends Integer> values) {
            for (Integer value : values) {
                value2key.put(value, key);
            }
            return key2Value.putAll(key, values);
        }

        @Override
        public boolean putAll(Multimap<? extends Integer, ? extends Integer> multimap) {
            for (Entry<? extends Integer, ? extends Integer> e : multimap.entries()) {
                value2key.put(e.getValue(), e.getKey());
            }
            return key2Value.putAll(multimap);
        }

        @Override
        public Collection<Integer> replaceValues(Integer key, Iterable<? extends Integer> values) {
            Collection<Integer> replaced = key2Value.replaceValues(key, values);
            for (Integer value : replaced) {
                value2key.remove(value, key);
            }
            for (Integer value : values) {
                value2key.put(value, key);
            }
            return replaced;
        }

        @Override
        public Collection<Integer> removeAll(Object key) {
            Collection<Integer> removed = key2Value.removeAll(key);
            for (Integer value : removed) {
                value2key.remove(value, key);
            }
            for (Integer reverse : value2key.removeAll(key)){
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
        public Collection<Integer> get(Integer key) {
            return key2Value.get(key);
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
        public Collection<Integer> values() {
            return key2Value.values();
        }

        @Override
        public Collection<Entry<Integer, Integer>> entries() {
            return key2Value.entries();
        }

        @Override
        public Map<Integer, Collection<Integer>> asMap() {
            return key2Value.asMap();
        }
}
