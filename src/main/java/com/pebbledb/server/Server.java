package com.pebbledb.server;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.pebbledb.actions.Action;
import com.pebbledb.events.DatabaseEventHandler;
import com.pebbledb.events.ExchangeEvent;
import com.pebbledb.events.PersistenceHandler;
import com.pebbledb.graphs.FastUtilGraph;
import com.pebbledb.graphs.Graph;
import io.undertow.Undertow;
import io.undertow.server.RoutingHandler;
import io.undertow.util.StatusCodes;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.pebbledb.server.Constants.*;

public class Server {

    private static Undertow undertow;
    private static final int THREADS = Runtime.getRuntime().availableProcessors();
    public static final Graph[] graphs = new Graph[Runtime.getRuntime().availableProcessors()];
    static RingBuffer<ExchangeEvent> ringBuffer;


    public Server() {
        for (int i = -1; ++i < graphs.length; ) {
            graphs[i]= new FastUtilGraph();
        }

        // Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 1024 * 2;
        Executor executor = Executors.newCachedThreadPool();

        // Construct the Disruptor
        WaitStrategy waitStrategy = new YieldingWaitStrategy();
        Disruptor<ExchangeEvent> disruptor = new Disruptor<>(ExchangeEvent::new, bufferSize, executor,
                ProducerType.SINGLE, waitStrategy);

        DatabaseEventHandler[] handlers = new DatabaseEventHandler[THREADS];
        for (int i = -1; ++i < THREADS; ) {
            handlers[i] = new DatabaseEventHandler(i);
        }

        // Connect the handlers
        disruptor.handleEventsWith(new PersistenceHandler())
                .then(handlers);

        // Start the Disruptor, get the ring buffer from the Disruptor to be used for publishing.
        ringBuffer = disruptor.start();
    }

    public static void main(final String[] args) {

        Server pebbleServer = new Server();
        pebbleServer.buildAndStartServer(8080, "localhost");
    }

    public void buildAndStartServer(int port, String host) {
        undertow = Undertow.builder()
                .addHttpListener(port, host)
                .setBufferSize(16 * 1024)
                .setWorkerThreads(1)
                .setIoThreads(1)
                .setHandler(new RoutingHandler()

                        .add(GET, PATH_REL_TYPES, new RequestHandler(false, Action.GET_RELATIONSHIP_TYPES))
                        .add(GET, PATH_REL_TYPES_COUNT, new RequestHandler(false, Action.GET_RELATIONSHIP_TYPES_COUNT))
                        .add(GET, PATH_REL_TYPE_COUNT, new RequestHandler(false, Action.GET_RELATIONSHIP_TYPE_COUNT))

                        .add(GET, "/db/test", e -> e.setStatusCode(StatusCodes.OK))
                        .add(GET, "/db/test2", new RequestHandler(false, Action.NOOP))
                        .add(GET, PATH_NODE, new RequestHandler(false, Action.GET_NODE))
                        .add(POST, PATH_NODE, new RequestHandler(true, Action.POST_NODE))
                        .add(DELETE, PATH_NODE, new RequestHandler(true, Action.DELETE_NODE))
                        .add(PUT, PATH_NODE_PROPERTIES, new RequestHandler(true, Action.PUT_NODE_PROPERTIES))
                        .add(DELETE, PATH_NODE_PROPERTIES, new RequestHandler(true, Action.DELETE_NODE_PROPERTIES))
                        .add(GET, PATH_NODE_PROPERTY, new RequestHandler(false, Action.GET_NODE_PROPERTY))
                        .add(PUT, PATH_NODE_PROPERTY, new RequestHandler(true, Action.PUT_NODE_PROPERTY))
                        .add(DELETE, PATH_NODE_PROPERTY, new RequestHandler(true, Action.DELETE_NODE_PROPERTY))

                        .add(GET, PATH_REL, new RequestHandler(false, Action.GET_RELATIONSHIP))
                        .add(POST, PATH_REL, new RequestHandler(true, Action.POST_RELATIONSHIP))
                        .add(DELETE, PATH_REL, new RequestHandler(true, Action.DELETE_RELATIONSHIP))
                        .add(PUT, PATH_REL_PROPERTIES, new RequestHandler(true, Action.PUT_RELATIONSHIP_PROPERTIES))
                        .add(DELETE, PATH_REL_PROPERTIES, new RequestHandler(true, Action.DELETE_RELATIONSHIP_PROPERTIES))
                        .add(GET, PATH_REL_PROPERTY, new RequestHandler(false, Action.GET_RELATIONSHIP_PROPERTY))
                        .add(PUT, PATH_REL_PROPERTY, new RequestHandler(true, Action.PUT_RELATIONSHIP_PROPERTY))
                        .add(DELETE, PATH_REL_PROPERTY, new RequestHandler(true, Action.DELETE_RELATIONSHIP_PROPERTY))
                        // Additional Relationships
                        .add(GET, PATH_ADD_REL, new RequestHandler(false, Action.GET_RELATIONSHIP))
                        .add(DELETE, PATH_ADD_REL, new RequestHandler(true, Action.DELETE_RELATIONSHIP))
                        .add(PUT, PATH_ADD_REL_PROPERTIES, new RequestHandler(true, Action.PUT_RELATIONSHIP_PROPERTIES))
                        .add(DELETE, PATH_ADD_REL_PROPERTIES, new RequestHandler(true, Action.DELETE_RELATIONSHIP_PROPERTIES))
                        .add(GET, PATH_ADD_REL_PROPERTY, new RequestHandler(false, Action.GET_RELATIONSHIP_PROPERTY))
                        .add(PUT, PATH_ADD_REL_PROPERTY, new RequestHandler(true, Action.PUT_RELATIONSHIP_PROPERTY))
                        .add(DELETE, PATH_ADD_REL_PROPERTY, new RequestHandler(true, Action.DELETE_RELATIONSHIP_PROPERTY))

                )
                .build();
        undertow.start();
    }

    public void stopServer() {
        if (undertow != null) {
            undertow.stop();
        }
    }

}
