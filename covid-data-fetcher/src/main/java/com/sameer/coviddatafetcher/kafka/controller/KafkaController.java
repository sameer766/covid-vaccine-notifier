package com.sameer.coviddatafetcher.kafka.controller;

import com.sameer.coviddatafetcher.pdf.PdfGenerationObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.sameer.coviddatafetcher.util.Util.KAFKA_TOPIC;

@RestController
public class KafkaController {


    //test controller
    @Autowired
    KafkaTemplate<String, PdfGenerationObject> kafkaTemplate;

    @GetMapping("/start")
    public String startPushing(){
        for (int i=0;i<10000;i++){
            kafkaTemplate.send(KAFKA_TOPIC, new PdfGenerationObject("s","s",1,"s"));
        }
        return "done";
    }
}
