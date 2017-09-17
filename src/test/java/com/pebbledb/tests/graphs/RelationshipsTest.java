package com.pebbledb.tests.graphs;

import com.pebbledb.graphs.Graph;
import com.pebbledb.graphs.FastUtilGraph;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.toIntExact;

public class RelationshipsTest {
    private Graph db;

    @Before
    public void setup() throws IOException {
        db = new FastUtilGraph();
        db.addNode("empty");
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("name", "max");
        properties.put("email", "maxdemarzi@hotmail.com");
        db.addNode("existing", properties);
        HashMap<String, Object> relProperties = new HashMap<>();
        relProperties.put("weight", 5);
        db.addRelationship("RELATED", "empty", "existing", relProperties);
    }

    @After
    public void tearDown() {
        db = null;
    }


    @Test
    public void shouldResolveIds() {
        int node1 = 7;
        int rel = 5;
        long combo;

        combo = node1;
        combo = combo << 32;
        combo += rel;
        int nodeId = toIntExact(combo >> 32);
        int relId = toIntExact((combo << 32) >> 32);
        Assert.assertEquals(nodeId, node1);
        Assert.assertEquals(rel, relId);

    }


    @Test
    public void shouldAddRelationship() {
        db.addNode("one");
        db.addNode("two");
        boolean created = db.addRelationship("FRIENDS", "one", "two");
        Assert.assertTrue(created);
    }

    @Test
    public void shouldAddRelationshipWithProperties() {
        HashMap<String, Object> properties = new HashMap<String, Object>() {{ put("stars", 5); }};
        db.addNode("one");
        db.addNode("two");
        db.addRelationship("RATED", "one", "two", properties);
        Object actual = db.getRelationship("RATED", "one", "two");
        Assert.assertEquals(properties, actual);
        Integer expected = 1;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("RATED"));
    }

    @Test
    public void shouldAddMultipleRelationshipsSameType() {
        db.addNode("one");
        db.addNode("two");
        db.addRelationship("MULTIPLE", "one", "two");
        boolean created = db.addRelationship("MULTIPLE", "one", "two");
        Assert.assertTrue(created);
        Integer expected = 2;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("MULTIPLE"));
        Assert.assertEquals(new HashMap<String, Object>(), db.getRelationship("MULTIPLE", "one", "two"));
        Assert.assertEquals(new HashMap<String, Object>(), db.getRelationship("MULTIPLE", "one", "two", 2));
    }

    @Test
    public void shouldAddMultipleRelationshipsSameTypeWithProperties() {
        db.addNode("one");
        db.addNode("two");
        Map rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1");}};
        Map rel2Properties = new HashMap<String, Object>() {{ put("key", "rel2");}};
        db.addRelationship("MULTIPLE", "one", "two", rel1Properties);
        boolean created = db.addRelationship("MULTIPLE", "one", "two", rel2Properties);
        Assert.assertTrue(created);
        Integer expected = 2;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("MULTIPLE"));
        Assert.assertEquals(rel1Properties, db.getRelationship("MULTIPLE", "one", "two"));
        Assert.assertEquals(rel2Properties, db.getRelationship("MULTIPLE", "one", "two", 2));
    }

    @Test
    public void shouldRemoveRelationship() {
        db.addNode("one");
        db.addNode("two");
        db.addRelationship("LIKES", "one", "two");
        Integer expected = 1;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("LIKES"));
        db.removeRelationship("LIKES", "one", "two");
        expected = 0;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("LIKES"));
    }

    @Test
    public void shouldRemoveMultipleRelationshipsSameType() {
        db.addNode("one");
        db.addNode("two");
        Map<String, Object> rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1");}};
        Map<String, Object> rel2Properties = new HashMap<String, Object>() {{ put("key", "rel2");}};
        Map<String, Object> rel3Properties = new HashMap<String, Object>() {{ put("key", "rel3");}};

        db.addRelationship("LOVES", "one", "two", rel1Properties);
        db.addRelationship("LOVES", "one", "two", rel2Properties);
        db.addRelationship("LOVES", "one", "two", rel3Properties);
        Integer expected = 3;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("LOVES"));
        db.removeRelationship("LOVES", "one", "two");
        expected = 2;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("LOVES"));
        Map<String, Object> rel = db.getRelationship("LOVES", "one", "two");
        Assert.assertEquals(rel1Properties, rel);
    }

    @Test
    public void shouldRemoveMultipleRelationshipsSameTypeLast() {
        db.addNode("one");
        db.addNode("two");
        Map<String, Object> rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1");}};
        Map<String, Object> rel2Properties = new HashMap<String, Object>() {{ put("key", "rel2");}};
        Map<String, Object> rel3Properties = new HashMap<String, Object>() {{ put("key", "rel3");}};

        db.addRelationship("LOVES", "one", "two", rel1Properties);
        db.addRelationship("LOVES", "one", "two", rel2Properties);
        db.addRelationship("LOVES", "one", "two", rel3Properties);
        Integer expected = 3;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("LOVES"));
        db.removeRelationship("LOVES", "one", "two", 3);
        expected = 2;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("LOVES"));
        Map<String, Object> rel = db.getRelationship("LOVES", "one", "two");
        Assert.assertEquals(rel1Properties, rel);
        rel = db.getRelationship("LOVES", "one", "two", 2);
        Assert.assertEquals(rel2Properties, rel);
    }

    @Test
    public void shouldRemoveMultipleRelationshipsSameTypeMiddle() {
        db.addNode("one");
        db.addNode("two");
        Map<String, Object> rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1");}};
        Map<String, Object> rel2Properties = new HashMap<String, Object>() {{ put("key", "rel2");}};
        Map<String, Object> rel3Properties = new HashMap<String, Object>() {{ put("key", "rel3");}};

        db.addRelationship("LOVES", "one", "two", rel1Properties);
        db.addRelationship("LOVES", "one", "two", rel2Properties);
        db.addRelationship("LOVES", "one", "two", rel3Properties);
        Integer expected = 3;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("LOVES"));
        db.removeRelationship("LOVES", "one", "two", 2);
        expected = 2;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("LOVES"));
        Map<String, Object> rel = db.getRelationship("LOVES", "one", "two");
        Assert.assertEquals(rel1Properties, rel);
        rel = db.getRelationship("LOVES", "one", "two", 2);
        Assert.assertEquals(rel3Properties, rel);
    }

    @Test
    public void shouldGetRelationshipWithoutProperties() {
        db.addNode("one");
        db.addNode("two");
        db.addRelationship("RATED", "one", "two");
        Object actual = db.getRelationship("RATED", "one", "two");
        Assert.assertEquals(new HashMap<>(), actual);
        Integer expected = 1;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("RATED"));
    }

    @Test
    public void shouldGetRelationshipWithProperties() {
        HashMap<String, Object> properties = new HashMap<String, Object>() {{ put("stars", 5); }};
        db.addNode("one");
        db.addNode("two");
        db.addRelationship("RATED", "one", "two", properties);
        Object actual = db.getRelationship("RATED", "one", "two");
        Assert.assertEquals(properties, actual);
        Integer expected = 1;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("RATED"));
    }

    @Test
    public void shouldGetMultipleRelationshipsSameType() {
        db.addNode("one");
        db.addNode("two");
        Map<String, Object> rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1");}};
        Map<String, Object> rel2Properties = new HashMap<String, Object>() {{ put("key", "rel2");}};

        db.addRelationship("MULTIPLE", "one", "two", rel1Properties);
        boolean created = db.addRelationship("MULTIPLE", "one", "two", rel2Properties);
        Assert.assertTrue(created);
        Integer expected = 2;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("MULTIPLE"));
        Assert.assertEquals(rel1Properties, db.getRelationship("MULTIPLE", "one", "two"));
        Assert.assertEquals(rel2Properties, db.getRelationship("MULTIPLE", "one", "two", 2));
    }

}
