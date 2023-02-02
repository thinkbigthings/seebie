package com.seebie.server.dto;

import java.time.ZonedDateTime;
import java.util.HashSet;

public record SleepDataWithId(Long id, SleepData sleepData) {

    public SleepDataWithId(Long id, String notes, int outOfBed, ZonedDateTime startTime, ZonedDateTime stopTime) {
        this(id, new SleepData(notes, outOfBed, new HashSet<>(), startTime, stopTime));
    }
}
