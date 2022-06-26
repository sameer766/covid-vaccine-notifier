package com.sameer.scheduler.service;


import com.sameer.scheduler.controller.AppController;
import com.sameer.scheduler.model.TimerInfo;
import com.sameer.scheduler.model.User;
import com.sameer.scheduler.model.VaccineRequest;
import com.sameer.scheduler.storage.controller.StorageController;
import com.sameer.scheduler.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class ScheduleService {

    @Autowired
    AppController appController;

    @Autowired
    StorageController storageController;

    @Autowired
    FileUtils fileUtils;

    public void schedule() {

//        IntStream.range(0,100).forEach((item)->
//        {
//            TimerInfo timerInfo = TimerInfo.builder().callbackData("string").cronExpression("0/7 * * * * ? *").initalOffset(0)
//                    .remainingFireCount(0).repeatIntervalMS(0).runForever(true).totalFireCount(0)
//                    .vaccineRequest(VaccineRequest.builder().age(item+25).pincode("522019")
//                            .userEmail("pandesameer76@gmail.com")
//                            .userPhoneNumber("+919479895240")
//                            .userName("Sameer pande")
//                            .build()).build();
//            appController.runGenericJob(timerInfo);
//        });
        Map<User, String> cronUserDetailsMap = null;
        try {
            cronUserDetailsMap = fileUtils.readFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        cronUserDetailsMap.forEach((item, cron) -> {
            TimerInfo timerInfo = TimerInfo.builder().callbackData("string").cronExpression(cron).initalOffset(0)
                    .remainingFireCount(0).repeatIntervalMS(0).runForever(true).totalFireCount(0)
                    .vaccineRequest(VaccineRequest.builder().age(item.getAge()).pincode(item.getPincode())
                            .userEmail(item.getUserEmail())
                            .userPhoneNumber(sanitizePhoneNumber(item.getUserPhoneNumber()))
                            .userName(item.getUserName())
                            .build()).build();
            appController.runGenericJob(timerInfo);
        });

    }

    private String sanitizePhoneNumber(String userPhoneNumber) {
        if (userPhoneNumber.startsWith("+91")) {
            return userPhoneNumber.length() == 13 ? userPhoneNumber : userPhoneNumber;
        } else if (userPhoneNumber.startsWith("91")) {
            return userPhoneNumber.length() == 12 ? "+" + userPhoneNumber : userPhoneNumber;
        } else {
            return userPhoneNumber.length() == 10 ? "+91" + userPhoneNumber : userPhoneNumber;
        }
    }

}