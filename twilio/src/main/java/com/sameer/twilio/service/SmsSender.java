package com.sameer.twilio.service;

public interface SmsSender {
  public boolean sendSms(SmsRequest smsRequest) throws InterruptedException;
}
