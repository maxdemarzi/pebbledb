package com.pebbledb.tests.graphs;

import com.pebbledb.graphs.FastUtilGraph;
import com.pebbledb.graphs.Graph;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NodesTest {
    private Graph db;

    @Before
    public void setup() throws IOException {
        db = new FastUtilGraph();
        db.addNode("empty");
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("name", "max");
        properties.put("email", "maxdemarzi@hotmail.com");
        db.addNode("existing", properties);
    }

    @After
    public void tearDown() {
        db = null;
    }

    @Test
    public void shouldAddNode() {
        boolean created = db.addNode("key");
        Assert.assertTrue(created);
        Assert.assertEquals(new HashMap<>(), db.getNode("key"));
    }

    @Test
    public void shouldAddNodeWithProperties() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("name", "max");
        properties.put("email", "maxdemarzi@hotmail.com");
        boolean created = db.addNode("max", properties);
        Assert.assertTrue(created);
        Assert.assertEquals(properties, db.getNode("max"));
    }

    @Test
    public void shouldRemoveNode() {
        boolean result = db.addNode("simple");
        Assert.assertTrue(result);
        result = db.removeNode("simple");
        Assert.assertTrue(result);
        Assert.assertTrue(db.getNode("simple") == null);
    }

    @Test
    public void shouldRemoveNodeRelationships() {
        db.addNode("one");
        db.addNode("two");
        db.addNode("three");
        db.addRelationship("FRIENDS", "one", "two");
        db.addRelationship("FRIENDS", "three", "one");

        boolean result = db.removeNode("one");
        Assert.assertTrue(result);
        Integer expected = 0;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("FRIENDS"));

        Assert.assertEquals(null, db.getRelationship("FRIENDS", "one", "two"));
        Assert.assertEquals(null, db.getRelationship("FRIENDS", "three", "one"));
    }

    @Test
    public void shouldAddNodeWithObjectProperties() {
        HashMap<String, Object> address = new HashMap<>();
        address.put("Country", "USA");
        address.put("Zip", "60601");
        address.put("State", "TX");
        address.put("City", "Chicago");
        address.put("Line1 ", "175 N. Harbor Dr.");
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("name", "max");
        properties.put("email", "maxdemarzi@hotmail.com");
        properties.put("address", address);
        boolean created = db.addNode("complex", properties);
        Assert.assertTrue(created);
        Assert.assertEquals(properties, db.getNode("complex"));
    }

    @Test
    public void shouldGetEmptyNode() {
        Assert.assertEquals(new HashMap<>(), db.getNode("empty"));
    }

    @Test
    public void shouldGetNodeWithProperties() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("name", "max");
        properties.put("email", "maxdemarzi@hotmail.com");
        Assert.assertEquals(properties, db.getNode("existing"));
    }

    @Test
    public void shouldGetNodeId() {
        int actual = db.getNodeId("empty");
        Assert.assertEquals(0, actual);
        actual = db.getNodeId("existing");
        Assert.assertEquals(1, actual);
    }

    @Test
    public void shouldNotAddNodeAlreadyThere() {
        boolean created = db.addNode("key");
        Assert.assertTrue(created);
        Assert.assertEquals(new HashMap<>(), db.getNode("key"));
        created = db.addNode("key");
        Assert.assertFalse(created);
    }

    @Test
    public void shouldNotGetNodeNotThere() {
        Assert.assertEquals(null, db.getNode("NotThere"));
    }

    @Test
    public void shouldNotRemoveNodeNotThere() {
        boolean result = db.removeNode("not_there");
        Assert.assertFalse(result);
    }

    @Test
    public void shouldNotGetNodeIdOfNodeNotThere() {
        int actual = db.getNodeId("not-empty");
        Assert.assertEquals(-1, actual);
    }

    @Test
    public void shouldGetAllNodes() {
        int count = 0;

        Iterator<Map<String, Object>> iterator = db.getAllNodes();
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        Assert.assertEquals(2, count);
    }

    @Test
    public void shouldClearGraph() {
        db.clear();
        int count = 0;
        Iterator<Map<String, Object>> iterator = db.getAllNodes();
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        Assert.assertEquals(0, count);
    }
}
