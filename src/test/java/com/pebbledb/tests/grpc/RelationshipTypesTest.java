package com.pebbledb.tests.grpc;

import com.pebbledb.proto.GetRelationshipTypesRequest;
import com.pebbledb.proto.GetRelationshipTypesResponse;
import com.pebbledb.proto.PebbleServerGrpc;
import com.pebbledb.server.PebbleServer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import static com.pebbledb.server.PebbleServer.graphs;

public class RelationshipTypesTest {
    private static PebbleServer server;
    private static ManagedChannel channel;
    private static PebbleServerGrpc.PebbleServerBlockingStub blockingStub;

    @Before
    public void setup() throws Exception {
        Config conf = ConfigFactory.load("pebble");
        server = new PebbleServer(conf);
        server.buildAndStartServer(conf);

        channel = ManagedChannelBuilder.forAddress("localhost", conf.getInt("pebble.grpc.port"))
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext(true)
                .build();
        blockingStub = PebbleServerGrpc.newBlockingStub(channel);

        for (int i = -1; ++i < graphs.length; ) {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("stars", 5);
            graphs[i].addNode("Node", "node1");
            graphs[i].addNode("Node", "node2");
            graphs[i].addNode("Node", "node3");
            graphs[i].addRelationship("FOLLOWS", "Node", "node1", "Node", "node2");
            graphs[i].addRelationship("FOLLOWS", "Node", "node1", "Node", "node3", properties);
        }
    }

    @After
    public void shutdown() throws InterruptedException {
        server.stopServer();
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
    public void integrationTestGetRelationshipTypes() {
        GetRelationshipTypesRequest request = GetRelationshipTypesRequest.newBuilder().build();
        GetRelationshipTypesResponse response;

        try {
            Iterator<GetRelationshipTypesResponse> iterator = blockingStub.getRelationshipTypes(request);
            while (iterator.hasNext()) {
                response = iterator.next();
                Assert.assertEquals("FOLLOWS", response.getResponse());
            }
        } catch (StatusRuntimeException e) {

        }
    }
}
