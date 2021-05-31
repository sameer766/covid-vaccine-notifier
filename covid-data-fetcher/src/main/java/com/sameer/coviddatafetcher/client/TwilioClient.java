package com.sameer.coviddatafetcher.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "twilio-api", url = "${twilio.ribbon.listOfServers}")
public interface TwilioClient {
    @GetMapping("/api/sendSms")
    public void sendSms(@RequestBody SmsRequest smsRequest);
}
