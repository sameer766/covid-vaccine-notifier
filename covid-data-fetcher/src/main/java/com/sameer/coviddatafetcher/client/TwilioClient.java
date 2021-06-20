package com.sameer.coviddatafetcher.client;

import com.sameer.coviddatafetcher.model.SmsRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "twilio-api", url = "${twilio.ribbon.listOfServers}")
public interface TwilioClient {
  @PostMapping("/api/sendSms")
  public boolean sendSms(@RequestBody SmsRequest smsRequest);
}
