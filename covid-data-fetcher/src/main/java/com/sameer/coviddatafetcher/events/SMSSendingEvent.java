package com.sameer.coviddatafetcher.events;

import com.sameer.coviddatafetcher.queue.QueueRequest;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.Future;

public class SMSSendingEvent extends ApplicationEvent {
    private Future<Boolean> booleanFuture;
    private QueueRequest queueRequest;
    public SMSSendingEvent(Object source) {
        super(source);
    }
    public SMSSendingEvent(Object source, Future<Boolean> booleanFuture, QueueRequest queueRequest) {
        super(source);
        this.booleanFuture=booleanFuture;
        this.queueRequest=queueRequest;
    }
    public QueueRequest getQueueObject() {
        return queueRequest;
    }

    public Future<Boolean> getMessage() {
        return booleanFuture;
    }

    @Override
    public String toString() {
        return "Custom event handler called";
    }
}
