package com.sameer.scheduler.model;

import com.sameer.scheduler.model.VaccineRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TimerInfo implements Serializable {
    private int totalFireCount;
    private int remainingFireCount;
    private boolean runForever;
    private long repeatIntervalMS;
    private long initalOffset;
    private String callbackData;
    private String cronExpression;
    private VaccineRequest vaccineRequest;
}
