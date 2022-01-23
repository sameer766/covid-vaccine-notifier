package com.sameer.twilio.service;


import com.sameer.twilio.configuration.TwilioConfiguration;
import com.twilio.rest.api.v2010.account.Message;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("twilio")
@Slf4j
public class TwilioSmsSender implements SmsSender {

  private final TwilioConfiguration twilioConfiguration;
  private final String PHONE_NUMBER_REGEX = "^[789]\\d{9}$";

  @Autowired
  public TwilioSmsSender(TwilioConfiguration twilioConfiguration) {
    this.twilioConfiguration = twilioConfiguration;
  }

  @Override
  public boolean sendSms(SmsRequest smsRequest) throws InterruptedException {
    if (isPhoneNumberValid(smsRequest.getPhoneNumber())) {
      log.info("request received for requestId : " + smsRequest.getRequestId());
      Message message = Message.creator(
          new com.twilio.type.PhoneNumber(smsRequest.getPhoneNumber()),
          new com.twilio.type.PhoneNumber(twilioConfiguration.getTrialNumber()),
          smsRequest.getMessage()).create();
      log.info(" message sent for requestId :" + smsRequest.getRequestId());
      return true;
    } else {
      throw new IllegalArgumentException("Phone number is not valid");
    }
  }

  private boolean isPhoneNumberValid(String phoneNumber) {
    String userPhoneNumber=phoneNumber;
    if(phoneNumber.length()>10)
      userPhoneNumber=phoneNumber.substring(userPhoneNumber.length()-10);
    Pattern pattern = Pattern.compile(PHONE_NUMBER_REGEX);
    Matcher matcher = pattern.matcher(userPhoneNumber);
    return matcher.matches();
  }
}
