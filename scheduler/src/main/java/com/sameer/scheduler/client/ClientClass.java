package com.sameer.scheduler.client;

import com.sameer.scheduler.info.TimerInfo;
import com.sameer.scheduler.job.Job1;
import com.sameer.scheduler.job.Job2;
import com.sameer.scheduler.service.TimerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientClass {

  @Autowired
  private TimerService timerService;

  public void run1(TimerInfo info) {
    timerService.schedule(Job1.class, info);
  }

  public void run2(TimerInfo info) {
    timerService.schedule(Job2.class, info);
  }

}