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
        reversibleMultiMap.put(1,1, 1);
        reversibleMultiMap.put(1,2, 2);
        reversibleMultiMap.put(3,4, 3);
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
        Assert.assertEquals(true, reversibleMultiMap.containsValue( (2L << 32) + 2));
        Assert.assertEquals(false, reversibleMultiMap.containsValue((9L << 32) + 2));
    }

    @Test
    public void shouldCheckRMMContainsEntry() {
        Assert.assertEquals(true, reversibleMultiMap.containsEntry(1,(2L << 32) + 2));
        Assert.assertEquals(false, reversibleMultiMap.containsEntry(9,(9L << 32) + 2));
    }

    @Test
    public void shouldCheckRMMPutAll() {
        Long four = (4L << 32) + 4;
        Long five = (5L << 32) + 5;
        reversibleMultiMap.putAll(3, new ArrayList<Long>(){{add(four); add(five);}});
        Assert.assertEquals(true, reversibleMultiMap.containsEntry(3, (4L << 32) + 4));
        Assert.assertEquals(false, reversibleMultiMap.containsEntry(3, (9L << 32) + 2));

        ReversibleMultiMap reversibleMultiMap2 = new ReversibleMultiMap();
        reversibleMultiMap2.put(8,8,1);
        reversibleMultiMap.putAll(reversibleMultiMap2);
        Assert.assertEquals(true, reversibleMultiMap.containsEntry(8,(8L << 32) + 1));
        Assert.assertEquals(false, reversibleMultiMap.containsEntry(3,(9L << 32) + 2));
    }

    @Test
    public void shouldCheckRMMReplaceValues() {
        Long seven = (7L << 32) + 7;
        Long eight = (8L << 32) + 8;
        reversibleMultiMap.putAll(3, new ArrayList<Long>(){{add(seven); add(eight);}});
        Assert.assertEquals(true, reversibleMultiMap.containsEntry(3, eight));
        Assert.assertEquals(false, reversibleMultiMap.containsEntry(9, (9L << 32) + 2));
    }

    @Test
    public void shouldCheckRMMClearAll() {
        Long eight = (8L << 32) + 12;
        ReversibleMultiMap reversibleMultiMap2 = new ReversibleMultiMap();
        reversibleMultiMap2.put(8,8, 12);
        Assert.assertEquals(true, reversibleMultiMap2.containsEntry(8,eight));
        reversibleMultiMap2.clear();
        Assert.assertEquals(false, reversibleMultiMap2.containsEntry(8,eight));
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
        Assert.assertArrayEquals(new Long[] { (1L << 32) + 1, (2L << 32) + 2, (4L << 32) + 3 } , reversibleMultiMap.values().toArray());
    }

    @Test
    public void shouldCheckRMMEntries() {
        Assert.assertEquals(3 , reversibleMultiMap.entries().size());
    }

    @Test
    public void shouldCheckRMMAsMap() {
        Map<Integer, Collection<Long>> expected = new HashMap<>();
        expected.put(1, new ArrayList<Long>() {{add((1L << 32) + 1); add((2L << 32) + 2);}});
        expected.put(3, new ArrayList<Long>() {{add((4L << 32) + 3); }});

        Assert.assertEquals(expected , reversibleMultiMap.asMap());
    }
}
