package com.pebbledb.tests.server;

import com.pebbledb.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;

import static com.pebbledb.server.Server.graphs;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

public class NodeTest {
    static Server server;

    @Before
    public void setup() throws Exception {
        server = new Server();
        server.buildAndStartServer();
        HashMap<String, Object> property =  new HashMap<String, Object>() {{ put("property", "Value"); }};
        HashMap<String, Object> props =  new HashMap<>();
        props.put("city", "Chicago");
        props.put("prop", property);

        for (int i = -1; ++i < graphs.length; ) {
            graphs[i].addNode("emptyNode");
            graphs[i].addNode("singlePropertyNode", property);
            graphs[i].addNode("complexPropertiesNode", props);
        }
    }

    @After
    public void shutdown() {
        server.stopServer();
    }

    @Test
    public void integrationTestGetNodeNotThere() {
        when().
                get("/db/node/notThere").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestGetEmptyNode() {
        when().
                get("/db/node/emptyNode").
                then().
                assertThat()
                .body(equalTo("{}"))
                .statusCode(200)
                .contentType("application/json");
    }

    @Test
    public void integrationTestGetSinglePropertyNode() {
        HashMap<String, Object> prop =  new HashMap<>();
        prop.put("property", "Value");

        when().
                get("/db/node/singlePropertyNode").
                then().
                assertThat()
                .body("$", equalTo(prop))
                .statusCode(200)
                .contentType("application/json");
    }

    @Test
    @Ignore
    public void integrationTestGetComplexPropertyNode() {
        HashMap<String, Object> prop =  new HashMap<>();
        prop.put("property", "Value");

        HashMap<String, Object> props =  new HashMap<>();
        props.put("city", "Chicago");
        props.put("prop", prop);

        when().
                get("/db/node/complexPropertiesNode").
                then().
                assertThat()
                .body("$", equalTo(props))
                .statusCode(200)
                .contentType("application/json;charset=UTF-8");
    }

    @Test
    public void integrationTestCreateEmptyNode() {
        given().
                contentType("application/json;charset=UTF-8").
                body("{}").
                when().
                post("/db/node/emptyNode").
                then().
                assertThat().
                body("$", equalTo(new HashMap<>())).
                statusCode(201).
                contentType("application/json");
    }

    @Test
    public void integrationTestCreateSinglePropertyNode() {
        HashMap<String, Object> prop =  new HashMap<>();
        prop.put("property", "Value");

        given().
                contentType("application/json;charset=UTF-8").
                body(prop).
                when().
                post("/db/node/singlePropertyNode").
                then().
                assertThat().
                body("$", equalTo(prop)).
                statusCode(201).
                contentType("application/json");
    }

    @Test
    @Ignore
    public void integrationTestCreateComplexPropertyNode() {
        HashMap<String, Object> prop =  new HashMap<>();
        prop.put("property", "Value");

        HashMap<String, Object> props =  new HashMap<>();
        props.put("city", "Chicago");
        props.put("prop", prop);

        given().
                contentType("application/json;charset=UTF-8").
                body(props).
                when().
                post("/db/node/complexPropertiesNode").
                then().
                assertThat().
                body("$", equalTo(props)).
                statusCode(201);
    }

    @Test
    public void integrationTestPutNodeNotThere() {
        when().
                put("/db/node/notThere/properties").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestUpdateSinglePropertyNode() {
        HashMap<String, Object> prop =  new HashMap<>();
        prop.put("name", "Value2");

        given().
                contentType("application/json;charset=UTF-8").
                body(prop).
                when().
                put("/db/node/singlePropertyNode/property/name").
                then().
                assertThat().
                statusCode(204);
    }

    @Test
    @Ignore
    public void integrationTestUpdateComplexPropertyNode() {
        HashMap<String, Object> prop =  new HashMap<>();
        prop.put("property", "Value");

        HashMap<String, Object> props =  new HashMap<>();
        props.put("city", "Miami");
        props.put("prop", prop);

        given().
                contentType("application/json;charset=UTF-8").
                body(props).
                when().
                put("/db/node/complexPropertiesNode").
                then().
                assertThat().
                body("$", equalTo(props)).
                statusCode(201);
    }

    @Test
    public void integrationTestDeleteNodeNotThere() {
        when().
                delete("/db/node/notThere").
                then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestDeleteEmptyNode() {
        when().
                delete("/db/node/emptyNode").
                then().
                assertThat().
                statusCode(204);
    }
}
