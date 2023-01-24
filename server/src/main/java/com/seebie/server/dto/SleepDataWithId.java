package com.seebie.server.dto;

import java.time.LocalDate;
import java.util.HashSet;

public record SleepDataWithId(Long id, SleepData sleepData) {

    public SleepDataWithId(Long id, LocalDate dateAwakened, int minutes, String notes, int outOfBed) {
        this(id, new SleepData(dateAwakened, minutes, notes, outOfBed, new HashSet<>()));
    }
}
