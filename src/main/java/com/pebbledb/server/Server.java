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
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.undertow.Undertow;
import io.undertow.server.RoutingHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.util.StatusCodes;

import javax.servlet.ServletException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.pebbledb.server.Constants.*;

public class Server {

    private static Undertow undertow;
    private static final int THREADS = Runtime.getRuntime().availableProcessors();
    public static final Graph[] graphs = new Graph[Runtime.getRuntime().availableProcessors()];
    static RingBuffer<ExchangeEvent> ringBuffer;
    static DeploymentManager manager;

    public Server() {
        Config conf = ConfigFactory.load("pebble");
        new Server(conf);
    }

    public Server(Config conf) {
        for (int i = -1; ++i < graphs.length; ) {
            graphs[i] = new FastUtilGraph();
        }

        Executor executor = Executors.newCachedThreadPool();

        // Construct the Disruptor
        WaitStrategy waitStrategy = new YieldingWaitStrategy(); //55k, 70% cpu
        //WaitStrategy waitStrategy = new SleepingWaitStrategy(5); // 50k, 40% cpu, slow test on empty
        //WaitStrategy waitStrategy = new TimeoutBlockingWaitStrategy(1, TimeUnit.SECONDS); // 37k, zero cpu
        //WaitStrategy waitStrategy = new BlockingWaitStrategy(); //35k, zero cpu
        //WaitStrategy waitStrategy = new BusySpinWaitStrategy();   //30k, high cpu, errors
        //WaitStrategy waitStrategy = new LiteBlockingWaitStrategy(); //37k, zero cpu
        //WaitStrategy waitStrategy = new LiteTimeoutBlockingWaitStrategy(1, TimeUnit.SECONDS); // 34k, zero cpu
        //WaitStrategy waitStrategy = new PhasedBackoffWaitStrategy(1, 1, TimeUnit.SECONDS, new SleepingWaitStrategy(5)); // 30k, half cpu, errors
        //WaitStrategy waitStrategy = new SleepingWaitStrategy(); // 50k, 40% cpu

        Disruptor<ExchangeEvent> disruptor = new Disruptor<>(ExchangeEvent::new, conf.getInt("pebble.disruptor.ring_buffer_size"), executor,
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

        DeploymentInfo servletBuilder = Servlets.deployment().setClassLoader(Server.class.getClassLoader())
                .setDeploymentName("openapi").setContextPath("/openapi")
                .addServlets(Servlets.servlet("openapi",
                        Bootstrap.class).addMapping("/openapi"));
        manager = Servlets.defaultContainer().addDeployment(servletBuilder);
        manager.deploy();
    }

    public static void main(final String[] args) throws ServletException {
        Config conf = ConfigFactory.load("pebble");
        Server pebbleServer = new Server(conf);
        pebbleServer.buildAndStartServer(conf);
    }

    public void buildAndStartServer() throws ServletException {
        Config conf = ConfigFactory.load("pebble");
        buildAndStartServer(conf);
}

    public void buildAndStartServer(Config conf) throws ServletException {

        undertow = Undertow.builder()
                .addHttpListener(conf.getInt("pebble.server.port"), conf.getString("pebble.server.host"))
                .setBufferSize(conf.getInt("pebble.server.buffer_size"))
                .setWorkerThreads(THREADS)
                .setIoThreads(1)
                .setHandler(new RoutingHandler()

                        .add(GET, "/db/test", e -> e.setStatusCode(StatusCodes.OK))
                        .add(GET, "/db/noop", new RequestHandler(false, Action.NOOP))

                        .add(GET, "/swagger", new SwaggerHandler())
                        .add(GET, "/openapi", manager.start())
                        .add(GET, "/webjars/*", new SwaggerUIHandler())

                        .add(GET, PATH_REL_TYPES, new RequestHandler(false, Action.GET_RELATIONSHIP_TYPES))
                        .add(GET, PATH_REL_TYPES_COUNT, new RequestHandler(false, Action.GET_RELATIONSHIP_TYPES_COUNT))
                        .add(GET, PATH_REL_TYPE_COUNT, new RequestHandler(false, Action.GET_RELATIONSHIP_TYPE_COUNT))

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

                        .add(GET, PATH_CONNECTED, new RequestHandler(false, Action.GET_RELATED))
                        .add(GET, PATH_CONNECTED_TYPE, new RequestHandler(false, Action.GET_RELATED))

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
