package com.sameer.twilio.service;

public interface SmsSender {
  boolean sendSms(SmsRequest smsRequest) throws InterruptedException;
}
