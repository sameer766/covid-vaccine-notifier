package com.sameer.scheduler.client;

import com.sameer.scheduler.job.Job3;
import com.sameer.scheduler.job.JobGeneric;
import com.sameer.scheduler.model.TimerInfo;
import com.sameer.scheduler.job.Job1;
import com.sameer.scheduler.job.Job2;
import com.sameer.scheduler.service.TimerService;
import org.quartz.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientClass {

  public static int GENERIC_CLASS_COUNT = 0;

  @Autowired
  private TimerService timerService;


  public void run1(TimerInfo info) {
    timerService.schedule(Job1.class, info);
  }

  public void run2(TimerInfo info) {
    timerService.schedule(Job2.class, info);
  }

  public void run3(TimerInfo timerInfo) {
    timerService.schedule(Job3.class, timerInfo);
  }

  public void runGenericJob(TimerInfo timerInfo) {
    Class<? extends Job> className1 = null;
    try {
      JobGeneric jobGeneric=new JobGeneric("job"+(++GENERIC_CLASS_COUNT));
      className1= (Class<? extends Job>) jobGeneric.getClassName();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    }
    timerService.schedule(className1, timerInfo);
  }

}