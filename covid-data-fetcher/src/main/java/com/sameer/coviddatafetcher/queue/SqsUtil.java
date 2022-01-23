package com.sameer.coviddatafetcher.queue;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.sameer.coviddatafetcher.events.EmailSendingEvent;
import com.sameer.coviddatafetcher.events.SMSSendingEvent;
import com.sameer.coviddatafetcher.model.VaccineRequest;
import com.sameer.coviddatafetcher.service.VaccineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Future;

import static com.sameer.coviddatafetcher.util.Util.EMAIL;
import static com.sameer.coviddatafetcher.util.Util.SMS;

@SpringBootApplication(exclude = {
        org.springframework.cloud.aws.autoconfigure.context.ContextInstanceDataAutoConfiguration.class,
        org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration.class,
        org.springframework.cloud.aws.autoconfigure.context.ContextRegionProviderAutoConfiguration.class
})
@RestController
public class SqsUtil implements ApplicationEventPublisherAware {

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    private AmazonSNSClient amazonSNSClient;

    @Autowired
    VaccineService vaccineService;

    ApplicationEventPublisher applicationEventPublisher;

    @Value("${cloud.aws.end-point-email.uri}")
    private String emailEndpoint;

    @Value("${cloud.aws.end-point-sms.uri}")
    public String smsEndPoint;

    @Value("${cloud.aws.end-point-topic.arn}")
    public String TOPIC_ARN;

    @GetMapping("/addSubscription/{email}")
    public String subscribeToEmail(@PathVariable String email) {
        SubscribeRequest subscribeRequest = new SubscribeRequest(TOPIC_ARN, "email", email);
        amazonSNSClient.subscribe(subscribeRequest);
        return "Succesfully susbcribed but please confirm";
    }

    @GetMapping("/sendNotification")
    public String publishMessage() {
        PublishRequest publishRequest = new PublishRequest(TOPIC_ARN, "There is network failure", "Connectivity Issue");
        amazonSNSClient.publish(publishRequest);
        return "Notification sent";
    }


    @GetMapping("/send-email/{message}")
    public void sendEmail(Integer requestId, VaccineRequest vaccineRequest, String message) {
        System.out.println("Email sent");
        queueMessagingTemplate.convertAndSend(emailEndpoint, new QueueRequest(requestId, vaccineRequest, message,EMAIL));
    }

    @GetMapping("/send-sms/{message}")
    public void sendSms(Integer requestId, VaccineRequest vaccineRequest, String message) {
        System.out.println("Message sent");
        queueMessagingTemplate.convertAndSend(smsEndPoint, new QueueRequest(requestId, vaccineRequest, message,SMS));
    }

    @SqsListener(value = "https://sqs.us-east-1.amazonaws.com/${app.queue.email}")
    public void loadMessageFromSqsEmail(QueueRequest queueRequest) {
        Future<Boolean> booleanFuture = vaccineService.sendEmail(queueRequest.getRequestId(), queueRequest.getVaccineRequest(), queueRequest.getMessage());
        System.out.println("Message from sqs queue : " + queueRequest);
        EmailSendingEvent customEvent = new EmailSendingEvent(this,booleanFuture,queueRequest);
        applicationEventPublisher.publishEvent(customEvent);
    }

    @SqsListener(value = "https://sqs.us-east-1.amazonaws.com/${app.queue.sms}")
    public void loadMessageFromSqsSms(QueueRequest queueRequest)  {
        Future<Boolean> booleanFuture = vaccineService.sendSms(queueRequest.getRequestId(), queueRequest.getVaccineRequest(), queueRequest.getMessage());
        System.out.println("Message from sqs queue : " + queueRequest);
        SMSSendingEvent customEvent = new SMSSendingEvent(this,booleanFuture,queueRequest);
        applicationEventPublisher.publishEvent(customEvent);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
