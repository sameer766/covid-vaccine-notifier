package com.sameer.coviddatafetcher.events;

import com.sameer.coviddatafetcher.queue.QueueRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@Component
public class SmsSendingEventListener implements ApplicationListener<SMSSendingEvent> {

    @Override
    public void onApplicationEvent(SMSSendingEvent customEvent) {
        Future<Boolean> message = customEvent.getMessage();
        QueueRequest queueRequest = customEvent.getQueueObject();
        handleMessageSendingCallbackResponse(message, queueRequest);
    }

    @CircuitBreaker(name = "sendsms", fallbackMethod = "handleMessageSendingCallbackResponseFallback")
    public void handleMessageSendingCallbackResponse(Future<Boolean> message, QueueRequest queueRequest) {
        try {
            if (message.get()) {
                log.info("sending {} notification success", queueRequest.getSource());
            } else {
                log.error("Error is sending {} notification for user {} with id {}", queueRequest.getSource(), queueRequest.getVaccineRequest().getUserName(), queueRequest.getRequestId());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void handleMessageSendingCallbackResponseFallback(Exception e) {
        System.out.println("This is a fallback method");
    }


}
