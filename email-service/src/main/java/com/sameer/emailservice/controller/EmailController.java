package com.sameer.emailservice.controller;

import com.sameer.emailservice.model.EmailRequest;
import com.sameer.emailservice.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

  @Autowired
  EmailService emailService;

  @PostMapping("/send-email")
  public boolean sendEmail(@RequestBody EmailRequest emailRequest) {
    try {
      return emailService.sendEmail(emailRequest);
    } catch (InterruptedException e) {
      e.printStackTrace();
      return false;
    }
  }
}
