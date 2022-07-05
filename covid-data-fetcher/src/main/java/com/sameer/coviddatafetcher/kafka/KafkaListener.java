package com.sameer.coviddatafetcher.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import com.sameer.coviddatafetcher.pdf.GeneratePdf;
import com.sameer.coviddatafetcher.pdf.PdfGenerationObject;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.sameer.coviddatafetcher.util.Util.KAFKA_TOPIC;

@Component
public class KafkaListener {


    @org.springframework.kafka.annotation.KafkaListener( topics = KAFKA_TOPIC, id = "group-id")
    public void listener(String data){

        ObjectMapper objectMapper=new ObjectMapper();
        try {
            PdfGenerationObject pdfGenerationObject = objectMapper.readValue(data, PdfGenerationObject.class);
            GeneratePdf.generatePdf(pdfGenerationObject.getUsername(), pdfGenerationObject.getPincode(), pdfGenerationObject.getAge(),pdfGenerationObject.getVaccineName());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Received data " + data);
    }
}
