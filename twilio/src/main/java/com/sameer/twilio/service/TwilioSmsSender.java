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

  @Autowired
  public TwilioSmsSender(TwilioConfiguration twilioConfiguration) {
    this.twilioConfiguration = twilioConfiguration;
  }

  @Override
  public boolean sendSms(SmsRequest smsRequest) {
    if(isPhoneNumberValid(smsRequest.getPhoneNumber()))
    {
      Message message = Message.creator(
          new com.twilio.type.PhoneNumber(smsRequest.getPhoneNumber()),
          new com.twilio.type.PhoneNumber(twilioConfiguration.getTrialNumber()),
          smsRequest.getMessage()).create();
      log.info(message+" message sent ");
      return true;
    }else {
      throw new IllegalArgumentException("Phone number is not valid");
    }
  }

  private boolean isPhoneNumberValid(String phoneNumber) {

    String patternString = "^[789]\\d{9}$";

    Pattern pattern = Pattern.compile(patternString);

    Matcher matcher = pattern.matcher(phoneNumber.substring(3));
    return matcher.matches();
  }
}
