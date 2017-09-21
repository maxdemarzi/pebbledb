package com.pebbledb.tests.server;

import com.pebbledb.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.when;

public class NoOpTest {
    static Server server;

    @Before
    public void setup() throws Exception {
        server = new Server();
        server.buildAndStartServer();
    }

    @After
    public void shutdown() {
        server.stopServer();
    }

    @Test
    public void integrationTestGetNoOp() {
        when().
                get("/db/noop").
                then().
                assertThat().
                statusCode(200);
    }
}
