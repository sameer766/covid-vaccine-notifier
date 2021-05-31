package com.sameer.coviddatafetcher.controller;

import com.sameer.coviddatafetcher.covid.VaccineRequest;
import com.sameer.coviddatafetcher.covid.VaccineResponse;
import com.sameer.coviddatafetcher.covid.VaccineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class CovidVaccineController {

    @Autowired
    VaccineService vaccineService;


    @PostMapping("/available-vaccine")
    public void getAvailableVaccine(@RequestBody VaccineRequest vaccineRequest) throws IOException {

        VaccineResponse vaccineResponse = vaccineService.getVaccineDetailsIfPresent(vaccineRequest);
        if (vaccineResponse != null) {
            vaccineService.notifyUser(vaccineRequest, vaccineResponse);
        }else
        {
            System.out.println("Vaccine not present");
        }
    }
}
