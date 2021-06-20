package com.sameer.coviddatafetcher.client;

import com.sameer.coviddatafetcher.model.EmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "email-api", url = "${email.ribbon.listOfServers}")
public interface EmailClient {
  @PostMapping("/send-email")
  public boolean sendEmail(@RequestBody EmailRequest emailRequest);
}
