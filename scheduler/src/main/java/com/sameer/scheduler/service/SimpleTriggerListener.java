package com.sameer.scheduler.service;

import com.sameer.scheduler.info.TimerInfo;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;

public class SimpleTriggerListener implements TriggerListener {

  private TimerService timerService;

  public SimpleTriggerListener(TimerService timerService) {
    this.timerService = timerService;
  }

  @Override
  public String getName() {
    return SimpleTriggerListener.class.getName();
  }

  @Override
  public void triggerFired(Trigger trigger, JobExecutionContext jobExecutionContext) {
    final String timerId = trigger.getKey().getName();
    JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
    TimerInfo timerInfo = (TimerInfo) jobDataMap.get(timerId);
    int remainingFireCount = timerInfo.getRemainingFireCount();
    if (!timerInfo.isRunForever() && remainingFireCount > 0) {
      timerInfo.setRemainingFireCount(remainingFireCount - 1);
    }
    timerService.updateTimer(timerId, timerInfo);
  }

  @Override
  public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
    return false;
  }

  @Override
  public void triggerMisfired(Trigger trigger) {

  }

  @Override
  public void triggerComplete(Trigger trigger,
                              JobExecutionContext context,
                              Trigger.CompletedExecutionInstruction triggerInstructionCode) {

  }
}
