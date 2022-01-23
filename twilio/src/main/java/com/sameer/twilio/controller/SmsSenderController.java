package com.sameer.twilio.controller;

import com.sameer.twilio.service.SmsRequest;
import com.sameer.twilio.service.SmsSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sendSms")
public class SmsSenderController {

  @Autowired
  SmsSenderService smsSenderService;

  @PostMapping
  public boolean sendSms(@RequestBody SmsRequest smsRequest) throws InterruptedException {
    return smsSenderService.sendSms(smsRequest);
  }
}

