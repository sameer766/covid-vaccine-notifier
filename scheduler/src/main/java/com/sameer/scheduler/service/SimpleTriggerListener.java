package com.sameer.scheduler.service;

import com.sameer.scheduler.model.TimerInfo;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;

//        users.forEach(item->
//        {
//            count++;
//            TimerInfo timerInfo = TimerInfo.builder().callbackData("string").cronExpression("0/10 * * * * ? *").initalOffset(0)
//                    .remainingFireCount(0).repeatIntervalMS(0).runForever(true).totalFireCount(0)
//                    .vaccineRequest(VaccineRequest.builder().age(item.getAge()).pincode(item.getPincode())
//                            .userEmail(item.getUserEmail())
//                            .userPhoneNumber(item.getUserPhoneNumber())
//                            .userName(item.getUserName())
//                            .build()).build();
//            appController.runJob3(timerInfo,"job"+ job.getAndIncrement());
//        });
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
