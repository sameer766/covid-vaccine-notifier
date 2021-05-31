package com.sameer.scheduler.info;

import com.sameer.scheduler.client.VaccineRequest;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
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
