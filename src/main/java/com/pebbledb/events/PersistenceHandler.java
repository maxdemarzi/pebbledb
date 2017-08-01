package com.pebbledb.events;

import com.lmax.disruptor.EventHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Methods;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.DocumentContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class PersistenceHandler implements EventHandler<ExchangeEvent> {
    private static final ChronicleQueue queue = SingleChronicleQueueBuilder.binary("events").build();
    private static final ExcerptAppender appender = queue.acquireAppender();

    public void onEvent(ExchangeEvent event, long sequence, boolean endOfBatch)
    {
        HttpServerExchange exchange = event.get();
        String path = exchange.getRequestPath();
        Map params = exchange.getQueryParameters();
        String body = null;

        boolean write = exchange.getRequestMethod().equals(Methods.POST);
        if (write) {
            BufferedReader reader = null;
            StringBuilder builder = new StringBuilder();

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
            body = builder.toString( );
        }
        event.setRequest(write, path, params, body);

        try (final DocumentContext dc = appender.writingDocument()) {
            dc.wire().write("path").text(path)
                    .write("parameters").object(params)
                    .write("body").text(body);
            System.out.println("Data was stored to index="+ dc.index());
        }

    }
}