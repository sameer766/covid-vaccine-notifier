package com.sameer.coviddatafetcher.events;

import com.sameer.coviddatafetcher.queue.QueueRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@Component
public class EmailSendingEventListener implements ApplicationListener<EmailSendingEvent> {
    @Override
    public void onApplicationEvent(EmailSendingEvent customEvent) {
        Future<Boolean> message = customEvent.getMessage();
        QueueRequest queueRequest = customEvent.getQueueObject();
        handleMessageSendingCallbackResponse(message, queueRequest);
    }

    private void handleMessageSendingCallbackResponse(Future<Boolean> message, QueueRequest queueRequest) {
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
}
