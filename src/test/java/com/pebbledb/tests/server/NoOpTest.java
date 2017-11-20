package com.pebbledb.tests.server;

import com.pebbledb.server.PebbleServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.when;

public class NoOpTest {
    static PebbleServer server;

    @Before
    public void setup() throws Exception {
        server = new PebbleServer();
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
