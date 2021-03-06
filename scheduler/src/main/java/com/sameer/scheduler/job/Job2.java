package com.sameer.scheduler.job;

import com.sameer.scheduler.client.CovidClient;
import com.sameer.scheduler.model.TimerInfo;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Job2 implements Job {
  @Autowired
  CovidClient covidClient;
  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
    TimerInfo timerInfo = (TimerInfo) jobDataMap.get(Job2.class.getSimpleName());
    log.info("Job2 :" + timerInfo.getCallbackData()+" executed at "+ LocalDateTime.now());
    covidClient.getAvailableVaccine(timerInfo.getVaccineRequest());
  }
}

