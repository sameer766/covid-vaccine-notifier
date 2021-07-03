package com.sameer.coviddatafetcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CovidDataFetcherApplication {

  public static void main(String[] args) {
    SpringApplication.run(CovidDataFetcherApplication.class, args);
  }

}
