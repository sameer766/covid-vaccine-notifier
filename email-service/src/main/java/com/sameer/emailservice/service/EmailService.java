package com.sameer.emailservice.service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.sameer.emailservice.model.EmailObject;
import com.sameer.emailservice.model.EmailRequest;
import com.sameer.emailservice.repository.EmailRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@Slf4j
//@EnableConfigurationProperties(AwsProperties.class)
public class EmailService {
  @Value("${aws.accessKeyId}")
  private String accessKey;
  @Value("${aws.secretKey}")
  private String awsSecretKey;

  @Autowired
  EmailRepo emailRepo;

  private static final String SUBJECT = "Covid Vaccine Available";
  private static final String FROM = "pandesameer76@gmail.com";

  public boolean sendEmail(EmailRequest emailRequest) throws InterruptedException {
    System.setProperty("aws.accessKeyId", accessKey);
    System.setProperty("aws.secretKey", awsSecretKey);
    AmazonSimpleEmailService amazonSimpleEmailService = AmazonSimpleEmailServiceClientBuilder.standard()
            .withRegion(Regions.US_EAST_1).build();


    SendEmailRequest request = new SendEmailRequest()
            .withDestination(
                    new Destination().withToAddresses(emailRequest.getUserEmail()))
            .withMessage(new Message()
                    .withBody(new Body()
                            .withHtml(new Content()
                                    .withCharset("UTF-8")
                                    .withData(emailRequest.getMessage())))
                    .withSubject(new Content()
                            .withCharset("UTF-8").withData(SUBJECT)))
            .withSource(FROM);
    log.info("Email request received for requestId : " + emailRequest.getRequestId());
     amazonSimpleEmailService.sendEmail(request);
    log.info("Email sent for requestId : " + emailRequest.getRequestId());

    Thread thread = new Thread(() -> {
      emailRepo.save(EmailObject.builder()
              .userName(emailRequest.getUserName())
              .userEmail(emailRequest.getUserEmail())
              .isEmailSent(Boolean.TRUE)
              .build());
    });
    thread.setDaemon(true);
    thread.start();
    return true;
  }

}