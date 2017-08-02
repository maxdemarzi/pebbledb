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

import java.util.Arrays;
import java.util.concurrent.ThreadFactory;

public class Server {

    public static final Graph[] graphs = new Graph[Runtime.getRuntime().availableProcessors()];
    public static RingBuffer<ExchangeEvent> ringBuffer;

    public static void main(final String[] args) throws InterruptedException {
        Arrays.fill(graphs, new FastUtilGraph());

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

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setBufferSize(1024 * 16)
                .setIoThreads(Runtime.getRuntime().availableProcessors() * 2) //this seems slightly faster in some configurations
                .setHandler(new RoutingHandler()
                    .add("GET", "/db/node/{id}", new NodeHandler(false, Action.GET_NODE))
                    .add("POST", "/db/node/{id}", new NodeHandler(true, Action.POST_NODE))
                    .add("PUT", "/db/node/{id}", new PutNodeHandler())
                    .add("DELETE", "/db/node/{id}", new DeleteNodeHandler())
                    .add("GET", "/db/node/{id}/property/{key}", new GetNodePropertyHandler())
                )
                .build();
        server.start();

    }

}
