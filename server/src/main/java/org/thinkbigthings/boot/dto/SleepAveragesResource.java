package org.thinkbigthings.boot.dto;

import static org.thinkbigthings.boot.dto.SleepResource.DATE_FORMAT;
import static org.thinkbigthings.sleep.SleepStatisticsCalculator.Group.ALL;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.thinkbigthings.sleep.SleepStatistics;
import org.thinkbigthings.sleep.SleepStatisticsCalculator.Group;

public class SleepAveragesResource implements SleepStatistics {

    @JsonIgnore private LocalDate groupEnding = null;
    private long numberSleepSessions = 0;
    private double efficiency = 0;
    private int allMinutes = 0;
    private int minutesSleeping = 0;
    private int minutesInBed = 0;
    private int minutesNapping = 0;
    
    @JsonCreator
    public SleepAveragesResource(@JsonProperty("groupEnding") String groupEndingStr,
            @JsonProperty("numberSleepSessions") long numberSleepSessions,
            @JsonProperty("efficiency") double efficiency,
            @JsonProperty("allMinutes") int allMinutes,
            @JsonProperty("minutesSleeping") int minutesSleeping,
            @JsonProperty("minutesInBed") int minutesInBed,
            @JsonProperty("minutesNapping") int minutesNapping) 
    {
        this.groupEnding = DATE_FORMAT.parseLocalDate(groupEndingStr);
        this.numberSleepSessions = numberSleepSessions;
        this.efficiency = efficiency;
        this.allMinutes = allMinutes;
        this.minutesSleeping = minutesSleeping;
        this.minutesInBed = minutesInBed;
        this.minutesNapping = minutesNapping;
    }

    
    public SleepAveragesResource(Collection<? extends SleepStatistics> set, Group group) {

        if (set.isEmpty()) {
            throw new IllegalArgumentException();
        }

        numberSleepSessions = set.size();
        DateTime latestEnding = new DateTime(0L);

        double sumEfficiency = 0;
        int sumAllMinutes = 0;
        int sumMinutesSleeping = 0;
        int sumMinutesInBed = 0;
        int sumMinutesNapping = 0;

        for (SleepStatistics sleep : set) {
            sumEfficiency += sleep.getEfficiency();
            sumAllMinutes += sleep.getAllMinutes();
            sumMinutesSleeping += sleep.getMinutesSleeping();
            sumMinutesInBed += sleep.getMinutesInBed();
            latestEnding = latestEnding.isAfter(sleep.getTimeOutOfBed()) ? latestEnding : sleep.getTimeOutOfBed();
        }

        efficiency = sumEfficiency / (double) numberSleepSessions;
        allMinutes = (int) Math.round((double) sumAllMinutes / (double) numberSleepSessions);
        minutesSleeping = (int) Math.round((double) sumMinutesSleeping / (double) numberSleepSessions);
        minutesInBed = (int) Math.round((double) sumMinutesInBed / (double) numberSleepSessions);
        minutesNapping = (int) Math.round((double) sumMinutesNapping / (double) numberSleepSessions);

        groupEnding = ALL.equals(group) ? latestEnding.toLocalDate() : group.getEndOfGroup(latestEnding);

    }

    public long getNumberSleepSessions() {
        return numberSleepSessions;
    }

    @Override
    public int getAllMinutes() {
        return allMinutes;
    }

    @Override
    public double getEfficiency() {
        return efficiency;
    }

    @Override
    public int getMinutesSleeping() {
        return minutesSleeping;
    }

    @Override
    public int getMinutesInBed() {
        return minutesInBed;
    }

    public String getGroupEnding() {
        return DATE_FORMAT.print(groupEnding);
    }

    @Override
    public int getMinutesNapping() {
        return minutesNapping;
    }

    @JsonIgnore
    @Override
    public DateTime getTimeOutOfBed() {
        return groupEnding.toDateTimeAtStartOfDay();
    }

}
