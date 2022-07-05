package com.sameer.scheduler.job;
import com.sameer.scheduler.client.CovidClient;
import com.sameer.scheduler.model.TimerInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class Job1 implements Job {

    @Autowired
    CovidClient covidClient;

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        TimerInfo timerInfo = (TimerInfo) jobDataMap.get(Job1.class.getSimpleName());
        log.info("Job1: " + timerInfo.getCallbackData() + " executed at " + LocalDateTime.now());

        //  log.info("Remaining time is : " + timerInfo.getRemainingFireCount());

        covidClient.getAvailableVaccine(timerInfo.getVaccineRequest());
    }
}
