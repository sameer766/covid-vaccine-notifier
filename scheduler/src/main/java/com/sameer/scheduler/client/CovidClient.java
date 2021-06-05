package com.sameer.scheduler.client;

import com.sameer.scheduler.model.VaccineRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "covid-api", url = "${covid.ribbon.listOfServers}")
public interface CovidClient {

    @PostMapping("/available-vaccine")
    public void getAvailableVaccine(VaccineRequest vaccineRequest);
}
