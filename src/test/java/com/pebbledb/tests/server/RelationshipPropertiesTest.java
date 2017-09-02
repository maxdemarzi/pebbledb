package com.pebbledb.tests.server;

import com.pebbledb.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static com.pebbledb.server.Server.graphs;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

public class RelationshipPropertiesTest {
    static Server server;

    @Before
    public void setup() throws Exception {
        server = new Server();
        server.buildAndStartServer(8080, "127.0.0.1");

        for (int i = -1; ++i < graphs.length; ) {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("stars", 5);
            properties.put("since", "2017-01-07");

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
    public void integrationTestGetRelationshipPropertyNotThere() {
        when().
                get("/db/relationship/FOLLOWS/node0/node1/property/not_there").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestGetRelationshipPropertyInvalidProperty() {
        when().
                get("/db/relationship/FOLLOWS/node1/node3/property/not_there").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestGetRelationshipProperty() {
        when().
                get("/db/relationship/FOLLOWS/node1/node3/property/since").
                then().
                assertThat().
                body(equalTo("\"2017-01-07\"")).
                statusCode(200).
                contentType("application/json");
    }

    @Test
    public void integrationTestGetRelationshipIntegerProperty() {
        when().
                get("/db/relationship/FOLLOWS/node1/node3/property/stars").
                then().
                assertThat().
                body(equalTo("5")).
                statusCode(200).
                contentType("application/json");
    }

    @Test
    public void integrationTestPutRelationshipPropertyNotThere() {
        given().
                contentType("application/json").
                body(2).
        when().
                put("/db/relationship/FOLLOWS/node0/node3/property/stars").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestPutRelationshipProperty() {
        HashMap<String, Object> prop = new HashMap<>();
        prop.put("archived", true);

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("stars", 5);
        properties.put("since", "2017-01-07");
        properties.put("archived", true);

        given().
                contentType("application/json").
                body(true).
                when().
                put("/db/relationship/FOLLOWS/node1/node3/property/archived").
                then().
                assertThat().
                statusCode(204);
    }

    @Test
    public void integrationTestPutRelationshipProperties() {
        HashMap<String, Object> prop = new HashMap<>();
        prop.put("stars", 4);
        prop.put("archived", true);

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("stars", 4);
        properties.put("since", "2017-01-07");
        properties.put("archived", true);

        given().
                contentType("application/json").
                body(prop).
                when().
                put("/db/relationship/FOLLOWS/node1/node3/properties").
                then().
                assertThat().
                body("$", equalTo(prop)).
                statusCode(201).
                contentType("application/json");
    }

    @Test
    public void integrationTestDeleteRelationshipPropertyNotThere() {
        when().
                delete("/db/relationship/FOLLOWS/node0/node1/property/not_there").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestDeleteRelationshipProperty() {
        when().
                delete("/db/relationship/FOLLOWS/node1/node3/property/since").
                then().
                assertThat().
                statusCode(204);
    }

    @Test
    public void integrationTestDeleteRelationshipPropertyInvalidProperty() {
        when().
                delete("/db/relationship/FOLLOWS/node1/node3/property/not_there").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestDeleteRelationshipProperties() {
        when().
                delete("/db/relationship/FOLLOWS/node1/node3/properties").
                then().
                assertThat().
                statusCode(204);
    }

}
