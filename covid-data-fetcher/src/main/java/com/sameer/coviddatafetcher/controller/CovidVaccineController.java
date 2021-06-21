package com.sameer.coviddatafetcher.controller;

import com.sameer.coviddatafetcher.model.OperationStatus;
import com.sameer.coviddatafetcher.model.Response;
import com.sameer.coviddatafetcher.model.VaccineRequest;
import com.sameer.coviddatafetcher.model.VaccineResponse;
import com.sameer.coviddatafetcher.service.VaccineService;
import java.io.IOException;
import java.text.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CovidVaccineController {

  private static final String EMPTY = "";

  @Autowired
  VaccineService vaccineService;

  @PostMapping("/available-vaccine")
  public Response getAvailableVaccine(@RequestBody VaccineRequest vaccineRequest) throws Exception {

    VaccineResponse vaccineResponse = vaccineService.getVaccineDetailsIfPresent(vaccineRequest);
    if (vaccineResponse != null) {
      boolean result = false;
      try {
        result = vaccineService.notifyUser(vaccineRequest, vaccineResponse);
      } catch (Exception e) {
        e.printStackTrace();
      }
      if (result) {
        return new Response(EMPTY, OperationStatus.SUCCESS.name());
      } else {
        return new Response("error in sending notification", OperationStatus.ERROR.name());
      }
    } else {
      return new Response(EMPTY, "Vaccine Not present");
    }
  }
}
