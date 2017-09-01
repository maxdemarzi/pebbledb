package com.pebbledb.tests.graphs;

import com.pebbledb.graphs.ReversibleMultiMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class ReversibleMultiMapTest {

    private ReversibleMultiMap<Integer> reversibleMultiMap;

    @Before
    public void setup() throws IOException {
        reversibleMultiMap = new ReversibleMultiMap<>();
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
    }
}
