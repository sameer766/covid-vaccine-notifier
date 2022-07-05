package com.sameer.serviceregsitry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer 
public class  ServiceRegsitryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceRegsitryApplication.class, args);
	}

}
