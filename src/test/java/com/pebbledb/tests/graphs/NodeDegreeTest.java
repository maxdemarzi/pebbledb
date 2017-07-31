package com.pebbledb.tests.graphs;

import com.pebbledb.Graph;
import com.pebbledb.graphs.FastUtilGraph;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class NodeDegreeTest {
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
    public void shouldGetNodeDegree() {
        db.addNode("four");
        db.addNode("five");
        db.addNode("six");
        db.addRelationship("FRIENDS", "four", "five");
        db.addRelationship("ENEMIES", "four", "six");
        Integer actual = db.getNodeDegree("four");
        Assert.assertEquals(Integer.valueOf(2), actual);
    }

    @Test
    public void shouldGetNodeIncomingDegree() {
        db.addNode("four");
        db.addNode("five");
        db.addNode("six");
        db.addRelationship("FRIENDS", "four", "five");
        db.addRelationship("ENEMIES", "six", "four");
        Integer actual = db.getNodeDegree("four", "out");
        Assert.assertEquals(Integer.valueOf(1), actual);
    }

    @Test
    public void shouldGetNodeOutgoingDegree() {
        db.addNode("four");
        db.addNode("five");
        db.addNode("six");
        db.addRelationship("FRIENDS", "four", "five");
        db.addRelationship("ENEMIES", "six", "four");
        Integer actual = db.getNodeDegree("four", "out");
        Assert.assertEquals(Integer.valueOf(1), actual);
    }

    @Test
    public void shouldGetNodeIncomingTypedDegree() {
        db.addNode("four");
        db.addNode("five");
        db.addNode("six");
        db.addRelationship("FRIENDS", "five", "four");
        db.addRelationship("ENEMIES", "six", "four");
        Integer actual = db.getNodeDegree("four", "in", new ArrayList<String>(){{add("ENEMIES");}});
        Assert.assertEquals(Integer.valueOf(1), actual);
    }

    @Test
    public void shouldGetNodeOutgoingTypedDegree() {
        db.addNode("four");
        db.addNode("five");
        db.addNode("six");
        db.addRelationship("FRIENDS", "four", "five");
        db.addRelationship("ENEMIES", "four", "six");
        Integer actual = db.getNodeDegree("four", "out", new ArrayList<String>(){{add("ENEMIES");}});
        Assert.assertEquals(Integer.valueOf(1), actual);
    }

    @Test
    public void shouldGetNodeDegreeMultiple() {
        db.addNode("four");
        db.addNode("five");
        db.addNode("six");
        db.addRelationship("FRIENDS", "four", "five");
        db.addRelationship("ENEMIES", "four", "six");
        db.addRelationship("FRIENDS", "four", "five");
        Integer actual = db.getNodeDegree("four");
        Assert.assertEquals(Integer.valueOf(3), actual);
    }

}
