package com.sameer.coviddatafetcher.controller;

import com.sameer.coviddatafetcher.model.OperationStatus;
import com.sameer.coviddatafetcher.model.Response;
import com.sameer.coviddatafetcher.model.VaccineRequest;
import com.sameer.coviddatafetcher.model.VaccineResponse;
import com.sameer.coviddatafetcher.service.VaccineService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import java.util.concurrent.Future;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CovidVaccineController {

  private static final String EMPTY = "";
  private static Integer requestId = 0;

  @Autowired
  VaccineService vaccineService;

  @Autowired
  CovidVaccineController covidVaccineController;

  @PostMapping("/available-vaccine")
  @RateLimiter(name = "service", fallbackMethod = "rateLimiterFallback")
  public Response getAvailableVaccine(@RequestBody VaccineRequest vaccineRequest) throws Exception {

    VaccineResponse vaccineResponse = vaccineService.getVaccineDetailsIfPresent(vaccineRequest);
    Future<Boolean> smsResponse = null;
    Future<Boolean> emailResponse = null;

    if (vaccineResponse != null) {
      try {
        String message = vaccineService.getMessage(vaccineRequest, vaccineResponse);
        smsResponse = vaccineService.sendSms(++requestId, vaccineRequest, message);
        emailResponse = vaccineService.sendEmail(requestId, vaccineRequest, message);
      } catch (Exception e) {
        e.printStackTrace();
      }
      if (smsResponse.get() && emailResponse.get()) {
        return new Response(EMPTY, OperationStatus.SUCCESS.name());
      } else {
        return new Response("Error in sending notification", OperationStatus.ERROR.name());
      }
    } else {
      return new Response(EMPTY, "Vaccine Not present");
    }
  }


  public Response rateLimiterFallback(VaccineRequest vaccineRequest,
                                      Exception e) {
    System.out.println("rateLimiterFallback for username : " + vaccineRequest.getUserName());
    return new Response(EMPTY, "Number of request more than given");
  }
}
