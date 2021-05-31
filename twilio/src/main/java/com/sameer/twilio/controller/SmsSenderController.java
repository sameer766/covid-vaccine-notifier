package com.sameer.twilio.controller;

import com.sameer.twilio.service.SmsRequest;
import com.sameer.twilio.service.SmsSender;
import com.sameer.twilio.service.SmsSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
  public void sendSms(@RequestBody SmsRequest smsRequest)
  {
    smsSenderService.sendSms(smsRequest);
  }

}
