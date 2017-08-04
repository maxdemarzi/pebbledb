package com.pebbledb.tests.server;

import com.pebbledb.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

import static com.pebbledb.server.Server.graphs;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

public class RelationshipTest {
    static Server server;

    @Before
    public void setup() throws IOException {
        server = new Server();
        server.buildAndStartServer(8080, "127.0.0.1");

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
    public void integrationTestGetRelationshipNotThere() {
        when().
                get("/db/relationship/FOLLOWS/node0/node1").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestGetEmptyRelationship() {
        when().
                get("/db/relationship/FOLLOWS/node1/node2").
                then().
                assertThat().
                body("$", equalTo(new HashMap<>())).
                statusCode(200).
                contentType("application/json");
    }

    @Test
    public void integrationTestGetSinglePropertyRelationship() {
        HashMap<String, Object> prop =  new HashMap<>();
        prop.put("stars", 5);

        when().
                get("/db/relationship/FOLLOWS/node1/node3").
                then().
                assertThat().
                body("$", equalTo(prop)).
                statusCode(200).
                contentType("application/json");
    }

    @Test
    public void integrationTestCreateEmptyRelationship() {
        given().
                contentType("application/json").
                when().
                post("/db/relationship/FOLLOWS/node2/node1").
                then().
                assertThat().
                body("$", equalTo(new HashMap<>())).
                statusCode(201).
                contentType("application/json");
    }

    @Test
    public void integrationTestCreateSinglePropertyRelationship() {
        HashMap<String, Object> prop =  new HashMap<>();
        prop.put("stars", 5);

        given().
                contentType("application/json").
                body(prop).
                when().
                post("/db/relationship/FOLLOWS/node1/node3").
                then().
                assertThat().
                body("$", equalTo(prop)).
                statusCode(201).
                contentType("application/json");
    }
    @Test
    public void integrationTestDeleteRelationshipNotThere() {
        when().
                delete("/db/relationship/NOT_THERE/node0/node1").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestDeleteRelationship() {
        when().
                delete("/db/relationship/FOLLOWS/node1/node2").
                then().
                assertThat().
                statusCode(204);
    }

}
