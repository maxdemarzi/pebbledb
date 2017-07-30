package com.pebbledb.tests;

import com.pebbledb.graphs.FastUtilGraph;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

public class FastUtilGraphTests {

    private FastUtilGraph db;
    
    @Before
    public void setup() throws IOException {
        db = new FastUtilGraph();
    }

    @Test
    public void shouldGetNodeOutgoingRelationshipNodeIds() {
        db.addNode("one");
        db.addNode("two");
        db.addNode("three");
        db.addRelationship("FRIENDS", "one", "two");
        db.addRelationship("FRIENDS", "one", "three");
        Object[] actual = db.getOutgoingRelationshipNodeIds("FRIENDS", "one");
        Object[] expected = new Object[2];
        expected[0] = db.getNodeId("two");
        expected[1] = db.getNodeId("three");

        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void shouldGetNodeIncomingRelationshipNodeIds() {
        db.addNode("one");
        db.addNode("two");
        db.addNode("three");
        db.addRelationship("FRIENDS", "two", "one");
        db.addRelationship("FRIENDS", "three", "one");
        Object[] actual = db.getIncomingRelationshipNodeIds("FRIENDS", "one");
        Object[] expected = new Object[2];
        expected[0] = db.getNodeId("two");
        expected[1] = db.getNodeId("three");

        Assert.assertArrayEquals(expected, actual);
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

//    @Test
//    public void shouldGetNodeDegree() {
//        db.addNode("four");
//        db.addNode("five");
//        db.addNode("six");
//        db.addRelationship("FRIENDS", "four", "five");
//        db.addRelationship("ENEMIES", "four", "six");
//        Integer actual = db.getNodeDegree("four");
//        Assert.assertEquals(Integer.valueOf(2), actual);
//    }
//
//    @Test
//    public void shouldGetNodeIncomingDegree() {
//        db.addNode("four");
//        db.addNode("five");
//        db.addNode("six");
//        db.addRelationship("FRIENDS", "four", "five");
//        db.addRelationship("ENEMIES", "six", "four");
//        Integer actual = db.getNodeDegree("four", "out");
//        Assert.assertEquals(Integer.valueOf(1), actual);
//    }
//
//    @Test
//    public void shouldGetNodeOutgoingDegree() {
//        db.addNode("four");
//        db.addNode("five");
//        db.addNode("six");
//        db.addRelationship("FRIENDS", "four", "five");
//        db.addRelationship("ENEMIES", "six", "four");
//        Integer actual = db.getNodeDegree("four", "out");
//        Assert.assertEquals(Integer.valueOf(1), actual);
//    }
//
//    @Test
//    public void shouldGetNodeIncomingTypedDegree() {
//        db.addNode("four");
//        db.addNode("five");
//        db.addNode("six");
//        db.addRelationship("FRIENDS", "five", "four");
//        db.addRelationship("ENEMIES", "six", "four");
//        Integer actual = db.getNodeDegree("four", "in", new ArrayList<String>(){{add("ENEMIES");}});
//        Assert.assertEquals(Integer.valueOf(1), actual);
//    }
//
//    @Test
//    public void shouldGetNodeOutgoingTypedDegree() {
//        db.addNode("four");
//        db.addNode("five");
//        db.addNode("six");
//        db.addRelationship("FRIENDS", "four", "five");
//        db.addRelationship("ENEMIES", "four", "six");
//        Integer actual = db.getNodeDegree("four", "out", new ArrayList<String>(){{add("ENEMIES");}});
//        Assert.assertEquals(Integer.valueOf(1), actual);
//    }

}
