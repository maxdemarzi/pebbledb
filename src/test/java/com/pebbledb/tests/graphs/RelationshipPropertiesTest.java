package com.pebbledb.tests.graphs;

import com.pebbledb.Graph;
import com.pebbledb.graphs.FastUtilGraph;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RelationshipPropertiesTest {

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

    @Test
    public void shouldGetRelationshipProperty() {
        Object property = db.getRelationshipProperty("RELATED", "empty", "existing", "weight");
        Assert.assertEquals(5, property);
    }

    @Test
    public void shouldNotGetRelationshipPropertyNotThere() {
        Object property = db.getRelationshipProperty("RELATED", "empty", "existing", "not_there");
        Assert.assertEquals(null, property);
    }

    @Test
    public void shouldNotGetRelationshipPropertyRelationshipNotTHere() {
        Object property = db.getRelationshipProperty("RELATED", "empty", "not_existing", "weight");
        Assert.assertEquals(null, property);
    }

    @Test
    public void shouldGetMultipleRelationshipProperty() {
        db.addNode("one");
        db.addNode("two");
        Map<String, Object> rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1");}};
        Map<String, Object> rel2Properties = new HashMap<String, Object>() {{ put("key", "rel2");}};
        Map<String, Object> rel3Properties = new HashMap<String, Object>() {{ put("key", "rel3");}};

        db.addRelationship("LOVES", "one", "two", rel1Properties);
        db.addRelationship("LOVES", "one", "two", rel2Properties);
        db.addRelationship("LOVES", "one", "two", rel3Properties);

        Object property = db.getRelationshipProperty("LOVES", "one", "two", "key");
        Assert.assertEquals("rel1", property);
        property = db.getRelationshipProperty("LOVES", "one", "two", 2,"key");
        Assert.assertEquals("rel2", property);
        property = db.getRelationshipProperty("LOVES", "one", "two", 3,"key");
        Assert.assertEquals("rel3", property);
    }

    @Test
    public void shouldNotGetMultipleRelationshipPropertyNotThere() {
        db.addNode("one");
        db.addNode("two");
        Map<String, Object> rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1");}};
        Map<String, Object> rel2Properties = new HashMap<String, Object>() {{ put("key", "rel2");}};
        Map<String, Object> rel3Properties = new HashMap<String, Object>() {{ put("key", "rel3");}};

        db.addRelationship("LOVES", "one", "two", rel1Properties);
        db.addRelationship("LOVES", "one", "two", rel2Properties);
        db.addRelationship("LOVES", "one", "two", rel3Properties);

        Object property = db.getRelationshipProperty("LOVES", "one", "two", "key");
        Assert.assertEquals("rel1", property);
        property = db.getRelationshipProperty("LOVES", "one", "two", 2,"not_there");
        Assert.assertEquals(null, property);
    }

    @Test
    public void shouldNotGetMultipleRelationshipPropertyRelationshipNotThere() {
        db.addNode("one");
        db.addNode("two");
        Map<String, Object> rel1Properties = new HashMap<String, Object>() {{ put("key", "rel1");}};
        Map<String, Object> rel2Properties = new HashMap<String, Object>() {{ put("key", "rel2");}};
        Map<String, Object> rel3Properties = new HashMap<String, Object>() {{ put("key", "rel3");}};

        db.addRelationship("LOVES", "one", "two", rel1Properties);
        db.addRelationship("LOVES", "one", "two", rel2Properties);
        db.addRelationship("LOVES", "one", "two", rel3Properties);

        Object property = db.getRelationshipProperty("LOVES", "one", "two", "key");
        Assert.assertEquals("rel1", property);
        property = db.getRelationshipProperty("LOVES", "one", "two", 4,"key");
        Assert.assertEquals(null, property);
    }

    
}
