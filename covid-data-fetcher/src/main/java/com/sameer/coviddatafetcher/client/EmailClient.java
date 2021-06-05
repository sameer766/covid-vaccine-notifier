package com.sameer.coviddatafetcher.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "email-api", url = "${email.ribbon.listOfServers}")
public interface EmailClient {
  @PostMapping("/send-email")
  public void sendEmail(@RequestBody EmailRequest emailRequest);
}
