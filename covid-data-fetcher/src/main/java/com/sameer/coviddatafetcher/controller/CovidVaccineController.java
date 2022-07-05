package com.sameer.coviddatafetcher.controller;

import com.sameer.coviddatafetcher.model.Response;
import com.sameer.coviddatafetcher.model.VaccineRequest;
import com.sameer.coviddatafetcher.model.VaccineResponse;
import com.sameer.coviddatafetcher.pdf.PdfGenerationObject;
import com.sameer.coviddatafetcher.queue.SqsUtil;
import com.sameer.coviddatafetcher.service.ContentService;
import com.sameer.coviddatafetcher.service.VaccineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.sameer.coviddatafetcher.util.Util.KAFKA_TOPIC;

@RestController
@RequestMapping
@Slf4j
public class CovidVaccineController {

    private static final String EMPTY = "";
    private static Integer requestId = 0;

    @Autowired
    VaccineService vaccineService;

    @Autowired
    ContentService contentService;

    @Autowired
    CovidVaccineController covidVaccineController;

    @Autowired
    SqsUtil sqsUtil;

    @Autowired
    KafkaTemplate<String, PdfGenerationObject> kafkaTemplate;

    @PostMapping("/available-vaccine")
    public Response getAvailableVaccine(@RequestBody VaccineRequest vaccineRequest) throws Exception {

        VaccineResponse vaccineResponse = vaccineService.getVaccineDetailsIfPresent(vaccineRequest);

        if (vaccineResponse != null) {
            try {
                String message = contentService.getMessage(vaccineRequest, vaccineResponse);
                saveUserDataAsync(vaccineRequest);
                saveApiResponseForPincodeAsync(vaccineRequest.getPincode(), vaccineResponse);
                PdfGenerationObject pdfGenerationObject = new PdfGenerationObject(vaccineRequest.getUserName(), vaccineRequest.getPincode(), vaccineRequest.getAge(), vaccineResponse.getVaccine());
                kafkaTemplate.send(KAFKA_TOPIC, pdfGenerationObject);

                sqsUtil.sendSms(++requestId, vaccineRequest, message);
                sqsUtil.sendEmail(requestId, vaccineRequest, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new Response(EMPTY, "Successfully pushed message to the queue");
        } else {
            log.info("Vaccine not present for pincode : " + vaccineRequest.getPincode());
            return new Response(EMPTY, "Vaccine Not present");
        }
    }

    private void saveApiResponseForPincodeAsync(String pincode, VaccineResponse vaccineResponse) {
        Thread thread = new Thread(() -> {
            vaccineService.saveApiResponseForPincode(pincode, vaccineResponse);
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void saveUserDataAsync(VaccineRequest vaccineRequest) {
        Thread thread = new Thread(() -> {
            vaccineService.saveUserData(vaccineRequest);
        });
        thread.setDaemon(true);
        thread.start();
    }
//
//    public Response rateLimiterFallback(VaccineRequest vaccineRequest,
//                                        Exception e) {
//        System.out.println("rateLimiterFallback for username : " + vaccineRequest.getUserName());
//        return new Response(EMPTY, "Number of request more than given");
//    }

    @GetMapping("/clearCache")
    public Response clearCache() {
        contentService.clear();
        return new Response(EMPTY, "Successfully cleared cache");
    }
}
