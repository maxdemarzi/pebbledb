package com.pebbledb.server;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.pebbledb.events.ClearingEventHandler;
import com.pebbledb.events.DatabaseEventHandler;
import com.pebbledb.events.ExchangeEvent;
import com.pebbledb.events.PersistenceHandler;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.util.concurrent.ThreadFactory;

public class Server {

    public static void main(final String[] args) throws InterruptedException {
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
        RingBuffer<ExchangeEvent> ringBuffer = disruptor.start();

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                        final long seq = ringBuffer.next();
                        //System.out.println("in handle request " + seq);
                        //System.out.println(exchange.getRequestURL());
                        final ExchangeEvent exchangeEvent = ringBuffer.get(seq);
                        exchangeEvent.set(exchange);
                        ringBuffer.publish(seq);
                        // This is deprecated but it works...
                        exchange.dispatch();
                        // Otherwise try this, but it creates a runnable.
                        //exchange.dispatch(null, () -> {});
                    }
                }).build();
        server.start();

    }

}
