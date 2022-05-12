package com.sameer.coviddatafetcher.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sameer.coviddatafetcher.client.EmailClient;
import com.sameer.coviddatafetcher.client.TwilioClient;
import com.sameer.coviddatafetcher.entity.PincodeDetails;
import com.sameer.coviddatafetcher.entity.UserInfo;
import com.sameer.coviddatafetcher.model.*;
import com.sameer.coviddatafetcher.repo.ContentRepo;
import com.sameer.coviddatafetcher.repo.PincodeRepo;
import com.sameer.coviddatafetcher.repo.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@Slf4j
public class VaccineService {

    private static final int MIN_WAIT_TIME_UPDATE_API_DATA = 5 ;
    private static Integer requestId = 0;

    ExecutorService executorService = Executors
            .newFixedThreadPool(Runtime.getRuntime()
            .availableProcessors());

    @Autowired
    TwilioClient twilioClient;

    @Autowired
    EmailClient emailClient;

    @Autowired
    ContentRepo contentRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    PincodeRepo pincodeRepo;

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
                        if (session.min_age_limit < vaccineRequest.getAge() && session.available_capacity > 0) {
                            requestId++;
                            log.info("Found vaccines " + requestId);

                            return VaccineResponse.builder()
                                    .isAvailable(isAvailable)
                                    .vaccine(session.getVaccine())
                                    .date(session.getDate())
                                    .slots(buildSlots(session.getSlots()))
                                    .build();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<String> buildSlots(List<Slots> slots) {
        List<String> response=new LinkedList<>();
        slots.forEach(slot -> {
            response.add("At time " + slot.getTime() + " with available seats " + slot.getSeats());
        });
        return response;
    }

    public Future<Boolean> sendSms(Integer requestId, VaccineRequest vaccineRequest, String message) {
        SmsRequest smsRequest = new SmsRequest(requestId, vaccineRequest.getUserPhoneNumber(), message);
        return executorService.submit(() -> twilioClient.sendSms(smsRequest));
    }

    public Future<Boolean> sendEmail(Integer requestId,
                                     VaccineRequest vaccineRequest,
                                     String message) {
        EmailRequest emailRequest = new EmailRequest(requestId,
                vaccineRequest.getUserName(),
                vaccineRequest.getUserEmail(),
                message);

        return executorService.submit(() -> emailClient.sendEmail(emailRequest));
    }


    public synchronized UserInfo saveUserData(VaccineRequest vaccineRequest) {
        Optional<UserInfo> userInfoOptional = userRepo.findByUserEmail(vaccineRequest.getUserEmail());
        if (!userInfoOptional.isPresent()) {
            UserInfo userInfo = UserInfo.builder()
                    .userEmail(vaccineRequest.getUserEmail())
                    .userName(vaccineRequest.getUserName())
                    .age(vaccineRequest.getAge())
                    .pincode(vaccineRequest.getPincode())
                    .userPhoneNumber(vaccineRequest.getUserPhoneNumber())
                    .build();
            UserInfo save = userRepo.save(userInfo);
            return save;
        }
        return userInfoOptional.get();
    }

    public synchronized void saveApiResponseForPincode(String pincode, VaccineResponse vaccineResponse) {
        Optional<PincodeDetails> byPincode = pincodeRepo.findByPincode(pincode);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (byPincode.isPresent()) {
                PincodeDetails pincodeDetails = byPincode.get();
                long seconds = ChronoUnit.SECONDS.between(pincodeDetails.getDateModified(), LocalDateTime.now());
                if (seconds> MIN_WAIT_TIME_UPDATE_API_DATA) {
                    pincodeDetails.setVaccineDetails(objectMapper.writeValueAsString(vaccineResponse));
                    pincodeRepo.saveAndFlush(pincodeDetails);
                }
            } else {
                pincodeRepo.save(new PincodeDetails(pincode, true, objectMapper.writeValueAsString(vaccineResponse)));
            }

        } catch (JsonProcessingException e) {
           e.printStackTrace();
        }

    }
}
