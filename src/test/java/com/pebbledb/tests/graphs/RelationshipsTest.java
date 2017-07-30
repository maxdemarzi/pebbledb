package com.pebbledb.tests.graphs;

import com.pebbledb.Graph;
import com.pebbledb.graphs.FastUtilGraph;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

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
        db.addRelationship("RELATED", "empty", "max", relProperties);
    }

    /*
        boolean addRelationship(String type, String from, String to);
    boolean addRelationship(String type, String from, String to, Map<String, Object> properties);
    boolean removeRelationship(String type, String from, String to);
    boolean removeRelationship(String type, String from, String to, int number);
    Map<String, Object> getRelationship(String type, String from, String to);
    Map<String, Object> getRelationship(String type, String from, String to, int number);
     */

    @Test
    public void shouldAddRelationship() {
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
    public void shouldRemoveRelationship() {
        db.addRelationship("LIKES", "one", "two");
        Integer expected = 1;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("LIKES"));
        db.removeRelationship("LIKES", "one", "two");
        expected = 0;
        Assert.assertEquals(expected, db.getRelationshipTypeCount("LIKES"));
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

}
