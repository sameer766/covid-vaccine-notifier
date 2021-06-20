package com.sameer.twilio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SmsSenderService {
  private final SmsSender smsSender;

  @Autowired
  public SmsSenderService(@Qualifier("twilio") TwilioSmsSender smsSender) {
    this.smsSender = smsSender;
  }

  public boolean sendSms(SmsRequest smsRequest) {
    return smsSender.sendSms(smsRequest);
  }
}
