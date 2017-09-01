package com.pebbledb.tests.graphs;

import com.pebbledb.graphs.ReversibleMultiMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class ReversibleMultiMapTest {

    private ReversibleMultiMap reversibleMultiMap;

    @Before
    public void setup() throws IOException {
        reversibleMultiMap = new ReversibleMultiMap();
        reversibleMultiMap.put(1,1);
        reversibleMultiMap.put(1,2);
        reversibleMultiMap.put(3,4);
    }

    @Test
    public void shouldCheckRMMSize() {
        Assert.assertEquals(3, reversibleMultiMap.size());
    }

    @Test
    public void shouldCheckRMMEmptyness() {
        Assert.assertEquals(false, reversibleMultiMap.isEmpty());
    }

    @Test
    public void shouldCheckRMMContainsKey() {
        Assert.assertEquals(true, reversibleMultiMap.containsKey(1));
        Assert.assertEquals(false, reversibleMultiMap.containsKey(9));
    }

    @Test
    public void shouldCheckRMMContainsValue() {
        Assert.assertEquals(true, reversibleMultiMap.containsValue(2));
        Assert.assertEquals(false, reversibleMultiMap.containsValue(9));
    }

    @Test
    public void shouldCheckRMMContainsEntry() {
        Assert.assertEquals(true, reversibleMultiMap.containsEntry(1,2));
        Assert.assertEquals(false, reversibleMultiMap.containsEntry(9,9));
    }

    @Test
    public void shouldCheckRMMPutAll() {
        reversibleMultiMap.putAll(3, new ArrayList<Integer>() {{add(4); add(5);}});
        Assert.assertEquals(true, reversibleMultiMap.containsEntry(3,4));
        Assert.assertEquals(false, reversibleMultiMap.containsEntry(3,9));

        ReversibleMultiMap reversibleMultiMap2 = new ReversibleMultiMap();
        reversibleMultiMap2.put(8,8);
        reversibleMultiMap.putAll(reversibleMultiMap2);
        Assert.assertEquals(true, reversibleMultiMap.containsEntry(8,8));
        Assert.assertEquals(false, reversibleMultiMap.containsEntry(3,9));
    }

    @Test
    public void shouldCheckRMMReplaceValues() {
        reversibleMultiMap.replaceValues(3, new ArrayList<Integer>() {{add(7); add(8);}});
        Assert.assertEquals(true, reversibleMultiMap.containsEntry(3,8));
        Assert.assertEquals(false, reversibleMultiMap.containsEntry(9,9));
    }

    @Test
    public void shouldCheckRMMClearAll() {
        ReversibleMultiMap reversibleMultiMap2 = new ReversibleMultiMap();
        reversibleMultiMap2.put(8,8);
        Assert.assertEquals(true, reversibleMultiMap2.containsEntry(8,8));
        reversibleMultiMap2.clear();
        Assert.assertEquals(false, reversibleMultiMap2.containsEntry(8,8));
    }

    @Test
    public void shouldCheckRMMKeySet() {
        Assert.assertEquals(new HashSet<Integer>(){{add(1); add(3);}}, reversibleMultiMap.keySet());
    }

    @Test
    public void shouldCheckRMMKeys() {
        Assert.assertArrayEquals(new Integer[] { 1, 1, 3 } , reversibleMultiMap.keys().toArray());
    }

    @Test
    public void shouldCheckRMMValues() {
        Assert.assertArrayEquals(new Integer[] { 1,2,4 } , reversibleMultiMap.values().toArray());
    }

    @Test
    public void shouldCheckRMMEntries() {
        Assert.assertEquals(3 , reversibleMultiMap.entries().size());
    }

    @Test
    public void shouldCheckRMMAsMap() {
        Map<Integer, Collection<Integer>> expected = new HashMap<>();
        expected.put(1, new ArrayList<Integer>() {{add(1); add(2);}});
        expected.put(3, new ArrayList<Integer>() {{add(4); }});

        Assert.assertEquals(expected , reversibleMultiMap.asMap());
    }
}
