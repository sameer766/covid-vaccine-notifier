package com.sameer.coviddatafetcher.events;

import com.sameer.coviddatafetcher.queue.QueueRequest;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.Future;

public class EmailSendingEvent extends ApplicationEvent {
    private Future<Boolean> booleanFuture;
    private QueueRequest queueRequest;
    public EmailSendingEvent(Object source) {
        super(source);
    }
    public EmailSendingEvent(Object source, Future<Boolean> booleanFuture, QueueRequest queueRequest) {
        super(source);
        this.booleanFuture=booleanFuture;
        this.queueRequest=queueRequest;
    }
    public QueueRequest getQueueObject() {
        return this.queueRequest;
    }

    public Future<Boolean> getMessage() {
        return this.booleanFuture;
    }

    @Override
    public String toString() {
        return "Custom event handler called";
    }
}
