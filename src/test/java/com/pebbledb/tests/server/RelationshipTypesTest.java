package com.pebbledb.tests.server;

import com.pebbledb.server.Server;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.pebbledb.server.Server.graphs;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

public class RelationshipTypesTest {
    static Server server;

    @Before
    public void setup() throws Exception {
        Config conf = ConfigFactory.load("pebble");
        server = new Server(conf);
        server.buildAndStartServer(conf);

        for (int i = -1; ++i < graphs.length; ) {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("stars", 5);
            graphs[i].addNode("node1");
            graphs[i].addNode("node2");
            graphs[i].addNode("node3");
            graphs[i].addRelationship("FOLLOWS", "node1", "node2");
            graphs[i].addRelationship("FOLLOWS", "node1", "node3", properties);
        }
    }

    @After
    public void shutdown() {
        server.stopServer();
    }

    @Test
    public void integrationTestGetRelationshipTypes() {
        when().
                get("/db/relationship_types").
                then().
                assertThat().
                body("$", equalTo(new ArrayList<String>(){{add("FOLLOWS");}})).
                statusCode(200).
                contentType("application/json");
    }

    @Test
    public void integrationTestGetRelationshipTypesCount() {
        when().
                get("/db/relationship_types/count").
                then().
                assertThat().
                body("$", equalTo(new HashMap<String, Integer>(){{ put("FOLLOWS", 2); }})).
                statusCode(200).
                contentType("application/json");
    }

    @Test
    public void integrationTestGetRelationshipTypeCount() {
        when().
                get("/db/relationship_types/FOLLOWS/count").
                then().
                assertThat().
                body(equalTo("2")).
                statusCode(200).
                contentType("application/json");
    }
}
