package com.sameer.scheduler.service;

import com.sameer.scheduler.model.TimerInfo;
import com.sameer.scheduler.utils.TimerUtils;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.sameer.scheduler.job.JobGeneric.myClassLoader;

@Service
@Slf4j
public class TimerService {

  Scheduler scheduler;

  @Autowired
  public TimerService(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  public <T extends Job> void schedule(final Class<T> jobClass, final TimerInfo info) {
    final JobDetail jobDetail = TimerUtils.buildJobDetail(jobClass, info);
    final Trigger trigger = TimerUtils.buildTrigger(jobClass, info);

    try {
      scheduler.scheduleJob(jobDetail, trigger);
    } catch (SchedulerException e) {
      log.error(e.getMessage(), e);
    }
  }

  public List<TimerInfo> getAllRunningJob() {
    try {
      return scheduler.getJobKeys(GroupMatcher.anyGroup())
              .stream()
              .map(jobKey ->
              {
                try {
                  final JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                  return (TimerInfo) jobDetail.getJobDataMap().get(jobKey.getName());
                } catch (SchedulerException e) {
                  e.printStackTrace();
                  return null;
                }
              })
              .filter(Objects::nonNull)
              .collect(Collectors.toList());

    } catch (SchedulerException e) {
      e.printStackTrace();
      return null;
    }
  }

  public TimerInfo getRunningTimer(String id) {
    try {
      final JobDetail jobDetail = scheduler.getJobDetail(new JobKey(id));
      return (TimerInfo) jobDetail.getJobDataMap().get(id);
    } catch (SchedulerException e) {
      e.printStackTrace();
      return null;
    }
  }

  public void updateTimer(String timerId, TimerInfo timerInfo) {
    try {
      final JobDetail jobDetail = scheduler.getJobDetail(new JobKey(timerId));
      jobDetail.getJobDataMap().put(timerId, timerInfo);
      scheduler.addJob(jobDetail, true);
    } catch (SchedulerException e) {
      e.printStackTrace();
    }
  }

  public boolean deleteTimer(String timerId) {
    try {
      boolean deleted = scheduler.deleteJob(new JobKey(timerId));
      System.out.println(myClassLoader);
      myClassLoader=null;
      if(deleted)
      {
        log.info("Successfuly deleted job with timerId :"+timerId);
      }
      return deleted;
    } catch (SchedulerException e) {
      e.printStackTrace();
    }
    return false;
  }


  @PostConstruct
  public void init() {
    try {
      scheduler.start();
      scheduler.getListenerManager().addTriggerListener(new SimpleTriggerListener(this));
      log.info("started");
    } catch (SchedulerException e) {
      e.printStackTrace();
    }
  }

  @PreDestroy
  public void preDestroy() {
    try {
      scheduler.shutdown();
      log.info("ended");
    } catch (SchedulerException e) {
      e.printStackTrace();
    }
  }

}
