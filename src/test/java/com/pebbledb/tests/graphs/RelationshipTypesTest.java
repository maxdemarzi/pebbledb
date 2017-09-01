package com.pebbledb.tests.graphs;

import com.pebbledb.graphs.Graph;
import com.pebbledb.graphs.FastUtilGraph;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class RelationshipTypesTest {
    private Graph db;

    @Before
    public void setup() throws IOException {
        db = new FastUtilGraph();
    }

    @After
    public void tearDown() {
        db = null;
    }

    @Test
    public void shouldGetRelationshipTypes() {
        db.addRelationship("FOLLOWS", "one", "two");
        Set<String> types = db.getRelationshipTypes();
        Assert.assertTrue(types.contains("FOLLOWS"));
    }

    @Test
    public void shouldGetRelationshipTypesCount() {
        db.addRelationship("FOLLOWS", "one", "two");
        Map<String, Integer> counts = db.getRelationshipTypesCount();
        Assert.assertTrue(counts.containsKey("FOLLOWS"));
        Assert.assertTrue(counts.get("FOLLOWS").equals(1));
    }

    @Test
    public void shouldGetRelationshipTypeCount() {
        db.addRelationship("FOLLOWS", "one", "two");
        Integer counts = db.getRelationshipTypeCount("FOLLOWS");
        Assert.assertTrue(counts.equals(1));
    }

}
