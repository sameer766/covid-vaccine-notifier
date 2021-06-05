package com.sameer.scheduler.controller;

import com.sameer.scheduler.model.TimerInfo;
import com.sameer.scheduler.service.TimerService;
import java.util.List;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AppController {

  @Autowired
  com.sameer.scheduler.client.ClientClass clientClass;
  @Autowired
  TimerService timerService;

  @PostMapping("/Job1")
  public void runJob1(@RequestBody TimerInfo timerInfo) {
    clientClass.run1(timerInfo);
  }

  @PostMapping("/Job2")
  public void runJob2(@RequestBody TimerInfo timerInfo) {
    clientClass.run2(timerInfo);
  }

  @GetMapping("/getAll")
  public List<TimerInfo> get() throws SchedulerException {
    return timerService.getAllRunningJob();
  }

  @GetMapping("/get/{id}")
  public TimerInfo getForId(@PathVariable String id) throws SchedulerException {
    return timerService.getRunningTimer(id);
  }

  @DeleteMapping("{id}")
  public boolean deleteTimer(@PathVariable String id) throws SchedulerException {
    return timerService.deleteTimer(id);
  }


}