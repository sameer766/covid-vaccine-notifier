package com.sameer.scheduler.utils;

import com.sameer.scheduler.model.TimerInfo;
import org.quartz.*;


public class TimerUtils {
  private TimerUtils() {

  }

  public static JobDetail buildJobDetail(final Class jobClass, final TimerInfo info) {
    final JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put(jobClass.getSimpleName(), info);

    return JobBuilder
        .newJob(jobClass)
        .storeDurably(true)
        .withIdentity(jobClass.getSimpleName())
        .setJobData(jobDataMap)
        .build();
  }

  public static Trigger buildTrigger(final Class jobClass, final TimerInfo info) {

    CronScheduleBuilder builder = CronScheduleBuilder.cronSchedule(info.getCronExpression());

    return TriggerBuilder
        .newTrigger()
        .withIdentity(jobClass.getSimpleName())
        .withSchedule(builder)
        .startNow()
        .build();
  }

}
