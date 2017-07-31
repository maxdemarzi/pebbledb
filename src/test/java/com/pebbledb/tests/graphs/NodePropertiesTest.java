package com.pebbledb.tests.graphs;

import com.pebbledb.Graph;
import com.pebbledb.graphs.FastUtilGraph;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NodePropertiesTest {
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
    
    @Test
    public void shouldGetNodeProperty() {
        Object property = db.getNodeProperty("existing", "name");
        Assert.assertEquals("max", property);
    }

    @Test
    public void shouldNotGetNodePropertyNotThere() {
        Object property = db.getNodeProperty("existing", "eman");
        Assert.assertEquals(null, property);
    }

    @Test
    public void shouldNotGetNodePropertyNodeNotThere() {
        Object property = db.getNodeProperty("not-existing", "name");
        Assert.assertEquals(null, property);
    }

    @Test
    public void shouldUpdateNodeProperties() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("property", "this");
        db.addNode("this", properties);
        properties.put("property", "that");
        db.updateNodeProperties("this", properties);
        Object property = db.getNodeProperty("this", "property");
        Assert.assertEquals("that", property);
    }

    @Test
    public void shouldDeleteNodeProperties() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("property", "this");
        db.addNode("this", properties);
        db.deleteNodeProperties("this");
        Assert.assertEquals(new HashMap<>(), db.getNode("this"));
    }

    @Test
    public void shouldUpdateNodeProperty() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("property", "this");
        db.addNode("this", properties);
        db.updateNodeProperty("this", "property", "that");
        Object property = db.getNodeProperty("this", "property");
        Assert.assertEquals("that", property);
    }

    @Test
    public void shouldDeleteNodeProperty() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("property", "this");
        properties.put("other", "that");
        db.addNode("this", properties);
        db.deleteNodeProperty("this", "other");
        Map<String, Object> node = db.getNode("this");
        Assert.assertEquals("this", node.get("property"));
        Assert.assertTrue(node.get("other") == null);
    }
    //// TODO: 7/30/17 Negative Tests
}
