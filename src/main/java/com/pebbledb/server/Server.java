package com.pebbledb.server;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.pebbledb.actions.Action;
import com.pebbledb.events.ClearingEventHandler;
import com.pebbledb.events.DatabaseEventHandler;
import com.pebbledb.events.ExchangeEvent;
import com.pebbledb.events.PersistenceHandler;
import com.pebbledb.graphs.FastUtilGraph;
import com.pebbledb.graphs.Graph;
import io.undertow.Undertow;
import io.undertow.server.RoutingHandler;

import java.util.concurrent.ThreadFactory;

public class Server {

    public static final Graph[] graphs = new Graph[Runtime.getRuntime().availableProcessors()];
    static RingBuffer<ExchangeEvent> ringBuffer;
    private Undertow server;

    public Server() {
        for (int i = -1; ++i < graphs.length; ) {
            graphs[i]= new FastUtilGraph();
        }

        // Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 1024;

        // Construct the Disruptor
        WaitStrategy waitStrategy = new BlockingWaitStrategy();
        Disruptor<ExchangeEvent> disruptor = new Disruptor<>(ExchangeEvent::new, bufferSize, (ThreadFactory) Thread::new,
                ProducerType.MULTI, waitStrategy);

        // Connect the handlers
        disruptor.handleEventsWith(new PersistenceHandler())
                .then(new DatabaseEventHandler())
                .then(new ClearingEventHandler());

        // Start the Disruptor, get the ring buffer from the Disruptor to be used for publishing.
        ringBuffer = disruptor.start();
    }

    public static void main(final String[] args) throws InterruptedException {

        Server pebbleServer = new Server();
        pebbleServer.buildAndStartServer(8080, "localhost");
    }

    public void buildAndStartServer(int port, String host) {
        server = Undertow.builder()
                .addHttpListener(port, host)
                .setBufferSize(1024 * 16)
                .setIoThreads(Runtime.getRuntime().availableProcessors() * 2) //this seems slightly faster in some configurations
                .setHandler(new RoutingHandler()

                        .add("GET", "/db/relationship_types", new RequestHandler(false, Action.GET_RELATIONSHIP_TYPES))
                        .add("GET", "/db/relationship_types/count", new RequestHandler(false, Action.GET_RELATIONSHIP_TYPES_COUNT))
                        .add("GET", "/db/relationship_types/{type}/count", new RequestHandler(false, Action.GET_RELATIONSHIP_TYPE_COUNT))

                        .add("GET", "/db/node/{id}", new RequestHandler(false, Action.GET_NODE))
                        .add("POST", "/db/node/{id}", new RequestHandler(true, Action.POST_NODE))
                        .add("DELETE", "/db/node/{id}", new RequestHandler(true, Action.DELETE_NODE))
                        .add("PUT", "/db/node/{id}/properties", new RequestHandler(true, Action.PUT_NODE_PROPERTIES))
                        .add("DELETE", "/db/node/{id}/properties", new RequestHandler(true, Action.DELETE_NODE_PROPERTIES))
                        .add("GET", "/db/node/{id}/property/{key}", new RequestHandler(false, Action.GET_NODE_PROPERTY))
                        .add("PUT", "/db/node/{id}/property/{key}", new RequestHandler(true, Action.PUT_NODE_PROPERTY))
                        .add("DELETE", "/db/node/{id}/property/{key}", new RequestHandler(true, Action.DELETE_NODE_PROPERTY))

                        .add("GET","/db/relationship/{type}/{from}/{to}", new RequestHandler(false, Action.GET_RELATIONSHIP))
                        .add("POST","/db/relationship/{type}/{from}/{to}", new RequestHandler(true, Action.POST_RELATIONSHIP))
                        .add("DELETE","/db/relationship/{type}/{from}/{to}", new RequestHandler(true, Action.DELETE_RELATIONSHIP))
                        .add("PUT","/db/relationship/{type}/{from}/{to}/properties", new RequestHandler(true, Action.PUT_RELATIONSHIP_PROPERTIES))
                        .add("DELETE","/db/relationship/{type}/{from}/{to}/properties", new RequestHandler(true, Action.DELETE_RELATIONSHIP_PROPERTIES))
                        .add("GET","/db/relationship/{type}/{from}/{to}/property/{key}", new RequestHandler(false, Action.GET_RELATIONSHIP_PROPERTY))
                        .add("PUT","/db/relationship/{type}/{from}/{to}/property/{key}", new RequestHandler(true, Action.PUT_RELATIONSHIP_PROPERTY))
                        .add("DELETE","/db/relationship/{type}/{from}/{to}/property/{key}", new RequestHandler(true, Action.DELETE_RELATIONSHIP_PROPERTY))
                        // Additional Relationships
                        .add("GET","/db/relationship/{type}/{from}/{to}/{number}", new RequestHandler(false, Action.GET_RELATIONSHIP))
                        .add("POST","/db/relationship/{type}/{from}/{to}", new RequestHandler(true, Action.POST_RELATIONSHIP))
                        .add("DELETE","/db/relationship/{type}/{from}/{to}/{number}", new RequestHandler(true, Action.DELETE_RELATIONSHIP))
                        .add("PUT","/db/relationship/{type}/{from}/{to}/{number}/properties", new RequestHandler(true, Action.PUT_RELATIONSHIP_PROPERTIES))
                        .add("DELETE","/db/relationship/{type}/{from}/{to}/{number}/properties", new RequestHandler(true, Action.DELETE_RELATIONSHIP_PROPERTIES))
                        .add("GET","/db/relationship/{type}/{from}/{to}/{number}/property/{key}", new RequestHandler(false, Action.GET_RELATIONSHIP_PROPERTY))
                        .add("PUT","/db/relationship/{type}/{from}/{to}/{number}/property/{key}", new RequestHandler(true, Action.PUT_RELATIONSHIP_PROPERTY))
                        .add("DELETE","/db/relationship/{type}/{from}/{to}/{number}/property/{key}", new RequestHandler(true, Action.DELETE_RELATIONSHIP_PROPERTY))

                )
                .build();
        server.start();
    }

    public void stopServer() {
        if (server != null) {
            server.stop();
        }
    }

}
