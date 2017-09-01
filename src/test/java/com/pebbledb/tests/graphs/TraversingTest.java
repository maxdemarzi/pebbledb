package com.pebbledb.tests.graphs;

import com.pebbledb.graphs.Graph;
import com.pebbledb.graphs.FastUtilGraph;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TraversingTest {
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
    public void shouldGetNodeOutgoingRelationshipNodeIds() {
        db.addNode("one");
        db.addNode("two");
        db.addNode("three");
        db.addRelationship("FRIENDS", "one", "two");
        db.addRelationship("FRIENDS", "one", "three");
        List<Integer> actual = db.getOutgoingRelationshipNodeIds("FRIENDS", "one");
        ArrayList<Integer> expected = new ArrayList<>();
        expected.add(db.getNodeId("two"));
        expected.add(db.getNodeId("three"));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetNodeIncomingRelationshipNodeIds() {
        db.addNode("one");
        db.addNode("two");
        db.addNode("three");
        db.addRelationship("FRIENDS", "two", "one");
        db.addRelationship("FRIENDS", "three", "one");
        List<Integer> actual = db.getIncomingRelationshipNodeIds("FRIENDS", "one");
        ArrayList<Integer> expected = new ArrayList<>();
        expected.add(db.getNodeId("two"));
        expected.add(db.getNodeId("three"));

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetNodeOutgoingRelationshipNodes() {
        HashMap<String, Object> node2props = new HashMap<String, Object> (){{ put("two", "node two"); }};
        HashMap<String, Object> node3props = new HashMap<String, Object> (){{ put("property1", 3); }};

        db.addNode("one");
        db.addNode("two", node2props);
        db.addNode("three", node3props);

        db.addRelationship("FRIENDS", "one", "two");
        db.addRelationship("FRIENDS", "one", "three");
        Object[] actual = db.getOutgoingRelationshipNodes("FRIENDS", "one");
        Object[] expected = new Object[2];
        expected[0] = node2props;
        expected[1] = node3props;

        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void shouldGetNodeIncomingRelationshipNodes() {
        HashMap<String, Object> node1props = new HashMap<String, Object> (){{ put("one", 1); }};
        HashMap<String, Object> node2props = new HashMap<String, Object> (){{ put("two", "node two"); }};
        HashMap<String, Object> node3props = new HashMap<String, Object> (){{ put("property1", 3); }};

        db.addNode("one", node1props);
        db.addNode("two", node2props);
        db.addNode("three", node3props);

        db.addRelationship("FRIENDS", "two", "one");
        db.addRelationship("FRIENDS", "three", "one");
        Object[] actual = db.getIncomingRelationshipNodes("FRIENDS", "one");
        Object[] expected = new Object[2];
        expected[0] = node2props;
        expected[1] = node3props;

        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void shouldGetNodeOutgoingRelationshipNodesByID() {
        HashMap<String, Object> node2props = new HashMap<String, Object> (){{ put("two", "node two"); }};
        HashMap<String, Object> node3props = new HashMap<String, Object> (){{ put("property1", 3); }};

        db.addNode("one");
        db.addNode("two", node2props);
        db.addNode("three", node3props);

        db.addRelationship("FRIENDS", "one", "two");
        db.addRelationship("FRIENDS", "one", "three");
        int one = db.getNodeId("one");
        Object[] actual = db.getOutgoingRelationshipNodes("FRIENDS", one);
        Object[] expected = new Object[2];
        expected[0] = node2props;
        expected[1] = node3props;

        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void shouldGetNodeIncomingRelationshipNodesByID() {
        HashMap<String, Object> node1props = new HashMap<String, Object> (){{ put("one", 1); }};
        HashMap<String, Object> node2props = new HashMap<String, Object> (){{ put("two", "node two"); }};
        HashMap<String, Object> node3props = new HashMap<String, Object> (){{ put("property1", 3); }};

        db.addNode("one", node1props);
        db.addNode("two", node2props);
        db.addNode("three", node3props);

        db.addRelationship("FRIENDS", "two", "one");
        db.addRelationship("FRIENDS", "three", "one");
        int one = db.getNodeId("one");
        Object[] actual = db.getIncomingRelationshipNodes("FRIENDS", one);
        Object[] expected = new Object[2];
        expected[0] = node2props;
        expected[1] = node3props;

        Assert.assertArrayEquals(expected, actual);
    }
}
