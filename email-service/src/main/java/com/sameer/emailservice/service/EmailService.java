package com.sameer.emailservice.service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.sameer.emailservice.model.EmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
//@EnableConfigurationProperties(AwsProperties.class)
public class EmailService {
  @Value("${aws.accessKeyId}")
  private String accessKey;
  @Value("${aws.secretKey}")
  private String awsSecretKey;

  private static final String SUBJECT = "Covid Vaccine Available";
  private static final String FROM = "pandesameer76@gmail.com";


  public void sendEmail(EmailRequest emailRequest) {
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
    amazonSimpleEmailService.sendEmail(request);
    System.out.println("Email sent!");
  }

}