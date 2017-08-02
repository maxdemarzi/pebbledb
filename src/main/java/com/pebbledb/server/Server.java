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
                .setBufferSize(1024 * 16)
                .setIoThreads(Runtime.getRuntime().availableProcessors() * 2) //this seems slightly faster in some configurations
                .setHandler(exchange -> {
                    final long seq = ringBuffer.next();
                    final ExchangeEvent exchangeEvent = ringBuffer.get(seq);
                    exchangeEvent.set(exchange);
                    ringBuffer.publish(seq);
                    // This is deprecated but it works...
                    exchange.dispatch();
                }).build();
        server.start();

    }

}
