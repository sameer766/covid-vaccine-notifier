package com.sameer.apigateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackMethodController {

    @PostMapping("/genericJobSchedulerFallback")
    public String genericJobSchedulerFallback(){
        return "Scheduler service is taking time";
    }
}

