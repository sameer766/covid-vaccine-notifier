package com.sameer.scheduler.controller;

import com.sameer.scheduler.model.User;
import com.sameer.scheduler.model.TimerInfo;
import com.sameer.scheduler.service.ScheduleService;
import com.sameer.scheduler.service.TimerService;

import java.io.IOException;
import java.util.List;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class AppController {

  @Autowired
  com.sameer.scheduler.client.ClientClass clientClass;
  @Autowired
  TimerService timerService;

  @Autowired
  ScheduleService scheduleService;

  @PostMapping("/Job1")
  public void runJob1(@RequestBody TimerInfo timerInfo) {
    clientClass.run1(timerInfo);
  }

  @PostMapping("/Job2")
  public void runJob2(@RequestBody TimerInfo timerInfo) {
    clientClass.run2(timerInfo);
  }

  @PostMapping("/Job3")
  public void runJob3(@RequestBody TimerInfo timerInfo) {
    clientClass.run3(timerInfo);
  }

  @PostMapping("/generic-job-scheduler/{className}")
  public void runJob3(@RequestBody TimerInfo timerInfo, @PathVariable String className) {
    clientClass.runGenericJob(timerInfo,className);
  }

  @PostMapping("/scheduleJobFromFile")
  public List<User> scheduleJob(@RequestParam("file") MultipartFile file) throws IOException {
    return scheduleService.schedule(file);
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