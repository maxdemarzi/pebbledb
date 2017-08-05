package com.pebbledb.events;

import com.lmax.disruptor.EventHandler;
import io.undertow.server.HttpServerExchange;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.DocumentContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PersistenceHandler implements EventHandler<ExchangeEvent> {
    private static final ChronicleQueue queue = SingleChronicleQueueBuilder.binary("events").build();
    private static final ExcerptAppender appender = queue.acquireAppender();

    public void onEvent(ExchangeEvent event, long sequence, boolean endOfBatch) {
//      System.out.println("Persistence Handler:" + sequence);
        event.setResponder((int) (sequence % 4));

        if(event.getWrite()) {

            BufferedReader reader = null;
            StringBuilder builder = new StringBuilder();

            HttpServerExchange exchange = event.get();

            try {
                exchange.startBlocking();
                reader = new BufferedReader(new InputStreamReader(exchange.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            event.setBody(builder.toString());

            try (final DocumentContext dc = appender.writingDocument()) {
                dc.wire().write("path").text(exchange.getRequestPath())
                        .write("body").text(event.getBody());
            }
        }
    }
}