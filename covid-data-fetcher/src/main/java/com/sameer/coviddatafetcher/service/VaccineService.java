package com.sameer.coviddatafetcher.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sameer.coviddatafetcher.client.EmailClient;
import com.sameer.coviddatafetcher.client.TwilioClient;
import com.sameer.coviddatafetcher.model.*;
import com.sameer.coviddatafetcher.repo.ContentRepo;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

  private static final Integer FIRST_CHAR_INDEX = 0;
  private static final String SPLITTER = " ";
  private String CONTENT = "";

  @Autowired
  TwilioClient twilioClient;

  @Autowired
  EmailClient emailClient;

  @Autowired
  ContentRepo contentRepo;

  //  @Autowired
//  Resilience4JCircuitBreakerFactory resilience4JCircuitBreakerFactory;
  CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.ofDefaults();

  private final CloseableHttpClient httpClient = HttpClients.createDefault();
  ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                                                           false);


  public VaccineResponse getVaccineDetailsIfPresent(VaccineRequest vaccineRequest)
      throws ParseException {

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

        boolean isAvailable = false;
        for (int i = 0; i < root.centers.size(); i++) {
          List<Session> sessions = root.centers.get(i).getSessions();
          for (int j = 0; j < sessions.size(); j++) {
            Session session = sessions.get(j);
            if (session.min_age_limit == 45 && session.available_capacity > 0) {
              log.info("Found vaccines");
              if (contentRepo.findByTemplate("MAIN").isPresent()) {
                CONTENT = contentRepo.findByTemplate("MAIN").get().getContent();
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


  public boolean notifyUser(VaccineRequest vaccineRequest, VaccineResponse vaccineResponse)

      throws IOException {


//    Resilience4JCircuitBreaker notify = resilience4JCircuitBreakerFactory.create("notify");



    List<String> slots = vaccineResponse.getSlots();
    String message = String.format(
        "Hi, "
            + getUserNameCamelCase(vaccineRequest.getUserName())
            + CONTENT,
        vaccineResponse.getDate(),
        slots.toString(),
        vaccineResponse.getVaccine(),
        vaccineRequest.getPincode());
    SmsRequest smsRequest = new SmsRequest(vaccineRequest.getUserPhoneNumber(), message);
//    notify.run(() -> twilioClient.sendSms(smsRequest), throwable -> handleErrorCase());
    CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();
    final CircuitBreaker circuitBreaker = registry.circuitBreaker("dd");


//    circuitBreaker.executeSupplier(()-> twilioClient.sendSms(smsRequest));
    twilioClient.sendSms(smsRequest);

    EmailRequest emailRequest = new EmailRequest(vaccineRequest.getUserEmail(),
                                                 message,
                                                 vaccineRequest.getUserEmail());
    emailClient.sendEmail(emailRequest);
    return true;
  }


  private String getUserNameCamelCase(String userName) {
    String[] userString = userName.split(SPLITTER);
    StringBuilder stringBuilder = new StringBuilder();
    for (String user : userString) {
      stringBuilder.append(Character.toUpperCase(user.charAt(FIRST_CHAR_INDEX)));
      stringBuilder.append(user.substring(FIRST_CHAR_INDEX + 1));
      stringBuilder.append(" ");
    }
    return stringBuilder.substring(0, stringBuilder.length() - 1);
  }
}
