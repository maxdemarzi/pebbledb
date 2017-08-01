package com.pebbledb.events;

import com.lmax.disruptor.EventHandler;

public class ClearingEventHandler<T> implements EventHandler<ExchangeEvent>
{
    public void onEvent(ExchangeEvent event, long sequence, boolean endOfBatch)
    {
        // Failing to call clear here will result in the
        // object associated with the event to live until
        // it is overwritten once the ring buffer has wrapped
        // around to the beginning.
        event.clear();
    }
}
