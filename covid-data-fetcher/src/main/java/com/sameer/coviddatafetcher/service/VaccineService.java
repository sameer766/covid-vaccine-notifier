package com.sameer.coviddatafetcher.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sameer.coviddatafetcher.client.EmailClient;
import com.sameer.coviddatafetcher.client.TwilioClient;
import com.sameer.coviddatafetcher.model.*;
import com.sameer.coviddatafetcher.repo.ContentRepo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VaccineService {

  private static Integer requestId = 0;
  private static final Integer FIRST_CHAR_INDEX = 0;
  private static final String SPLITTER = " ";
  private String CONTENT = "";
  private static final String TEMPLATE_NAME = "MAIN";

  ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
                                                                     .availableProcessors());

  @Autowired
  TwilioClient twilioClient;

  @Autowired
  EmailClient emailClient;

  @Autowired
  ContentRepo contentRepo;


  private final CloseableHttpClient httpClient = HttpClients.createDefault();
  ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                                                           false);


  public VaccineResponse getVaccineDetailsIfPresent(VaccineRequest vaccineRequest)
      throws Exception {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    String todaysDate = formatter.format(LocalDate.now());
    HttpGet request = new HttpGet(
        "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin?pincode="
            + vaccineRequest.getPincode()
            + "&date="
            + todaysDate);

    request.addHeader("authority", "cdn-api.co-vin.in");
    request.addHeader("sec-ch-ua",
                      "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"90\", \"Google Chrome\";v=\"90\"");
    request.addHeader("accept", "application/json, text/plain, */*");
    request.addHeader("sec-ch-ua-mobile", "?0");
    request.addHeader("user-agent",
                      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36");
    request.addHeader("origin", "https://www.cowin.gov.in");
    request.addHeader("sec-fetch-mode", "cors");
    request.addHeader("sec-fetch-dest", "empty");
    request.addHeader("referer", "https://www.cowin.gov.in/");
    request.addHeader("accept-language", "en-GB,en-US;q=0.9,en;q=0.8");
    request.addHeader("if-none-match", "W/\"755-kSDAGwS0dhuJu/VuZ3UJpZ2STnc\"");

    try (CloseableHttpResponse response = httpClient.execute(request)) {


      HttpEntity entity = response.getEntity();

      if (entity != null) {
        // return it as a String
        String result = EntityUtils.toString(entity);
        Root root = objectMapper.readValue(result, Root.class);

        if (root.getError() != null) {
          throw new Exception(root.getError());
        }
        boolean isAvailable = false;
        for (int i = 0; i < root.centers.size(); i++) {
          List<Session> sessions = root.centers.get(i).getSessions();
          for (int j = 0; j < sessions.size(); j++) {
            Session session = sessions.get(j);
            if (session.min_age_limit == 45 && session.available_capacity > 0) {
              requestId++;
              log.info("Found vaccines " + requestId);
              if (contentRepo.findByTemplate(TEMPLATE_NAME).isPresent()) {
                CONTENT = contentRepo.findByTemplate(TEMPLATE_NAME).get().getContent();
              }
              return VaccineResponse.builder()
                  .isAvailable(isAvailable)
                  .vaccine(session.getVaccine())
                  .date(session.getDate())
                  .slots(session.getSlots())
                  .build();
            }
          }
        }

      }

    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Retry(name = "service", fallbackMethod = "retryFallback")
  @CircuitBreaker(name = "service", fallbackMethod = "circuitBreakerFallback")
  public Future<Boolean> sendSms(Integer requestId, VaccineRequest vaccineRequest, String message) throws ExecutionException, InterruptedException {
    SmsRequest smsRequest = new SmsRequest(requestId, vaccineRequest.getUserPhoneNumber(), message);
    return executorService.submit(() -> twilioClient.sendSms(smsRequest));
  }

  @Retry(name = "service", fallbackMethod = "retryFallback")
  @CircuitBreaker(name = "service", fallbackMethod = "circuitBreakerFallback")
  public Future<Boolean> sendEmail(Integer requestId,
                                   VaccineRequest vaccineRequest,
                                   String message) {
    EmailRequest emailRequest = new EmailRequest(requestId,
                                                 vaccineRequest.getUserEmail(),
                                                 message);

    return executorService.submit(() -> emailClient.sendEmail(emailRequest));
  }

  public Future<Boolean> circuitBreakerFallback(Integer requestId, VaccineRequest vaccineRequest,
                                                String message,
                                                Exception e) {
    System.out.println("circuitBreakerFallback for requestId :" + requestId);
    return CompletableFuture.completedFuture(false);
  }

  public Future<Boolean> retryFallback(Integer requestId,
                                       VaccineRequest vaccineRequest,
                                       String message,
                                       Exception e) {
    System.out.println("retryFallback for requestId :" + requestId);
    return CompletableFuture.completedFuture(false);
  }


  public String getUserNameCamelCase(String userName) {
    String[] userString = userName.split(SPLITTER);
    StringBuilder stringBuilder = new StringBuilder();
    for (String user : userString) {
      stringBuilder.append(Character.toUpperCase(user.charAt(FIRST_CHAR_INDEX)));
      stringBuilder.append(user.substring(FIRST_CHAR_INDEX + 1));
      stringBuilder.append(" ");
    }
    //ignore last space
    return stringBuilder.substring(0, stringBuilder.length() - 1);
  }


  public String getMessage(VaccineRequest vaccineRequest, VaccineResponse vaccineResponse) {
    List<String> slots = vaccineResponse.getSlots();
    return String.format(
        "Hi, "
            + getUserNameCamelCase(vaccineRequest.getUserName())
            + CONTENT,
        vaccineResponse.getDate(),
        slots.toString(),
        vaccineResponse.getVaccine(),
        vaccineRequest.getPincode());
  }


}
