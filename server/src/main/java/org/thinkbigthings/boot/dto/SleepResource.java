package org.thinkbigthings.boot.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.hateoas.ResourceSupport;

public class SleepResource extends ResourceSupport {

    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm a zzz");

    @JsonIgnore private DateTime startTime;
    @JsonIgnore private DateTime finishTime;
    private int minutesNapping = 0;
    private int minutesAwakeInBed = 0;
    private int minutesAwakeNotInBed = 0;

    @JsonCreator
    public SleepResource(@JsonProperty("startTime") String startStr,
            @JsonProperty("finishTime") String finishStr,
            @JsonProperty("minutesNapping") int mn,
            @JsonProperty("minutesAwakeInBed") int mib,
            @JsonProperty("minutesAwakeNotInBed") int mob) {
        startTime = DATE_TIME_FORMAT.parseDateTime(startStr);
        finishTime = DATE_TIME_FORMAT.parseDateTime(finishStr);
        minutesNapping = mn;
        minutesAwakeInBed = mib;
        minutesAwakeNotInBed = mob;
    }

   // TODO implement SleepStatistics? maybe don't want SleepStatistics to use Date
    public int getAllMinutes() {
        return Minutes.minutesBetween(startTime, finishTime).getMinutes();
    }

    public int getMinutesInBed() {
        return getAllMinutes() - minutesAwakeNotInBed;
    }

    public int getMinutesSleeping() {
        return getMinutesInBed() - minutesAwakeInBed;
    }

    /**
     *
     * @return a decimal number between 0 and 100 representing the sleep efficiency as a percentage.
     */
    public double getEfficiency() {
        return 100 * (double) getMinutesSleeping() / (double) getMinutesInBed();
    }

    /**
     * Utility method for client editing
     *
     * @param hour
     * @param minute
     */
    public void setStartTime(int hour, int minute) {
        startTime = startTime.withHourOfDay(hour).withMinuteOfHour(minute);
    }

    /**
     * Utility method for client editing
     *
     * @param hour
     * @param minute
     */
    public void setFinishTime(int hour, int minute) {
        finishTime = finishTime.withHourOfDay(hour).withMinuteOfHour(minute);
    }

    /**
     * Utility method for client editing
     *
     * @param year
     * @param month
     * @param day
     */
    public void setFinishDate(int year, int month, int day) {
        finishTime = finishTime.withYear(year).withMonthOfYear(month).withDayOfMonth(day);
    }

    public String getStartTime() {
        return DATE_TIME_FORMAT.print(startTime);
    }

    public void setStartTime(String start) {
        startTime = DATE_TIME_FORMAT.parseDateTime(start);
    }

    public String getFinishTime() {
        return DATE_TIME_FORMAT.print(finishTime);
    }

    public void setFinishTime(String finish) {
        finishTime = DATE_TIME_FORMAT.parseDateTime(finish);
    }

    public int getMinutesNapping() {
        return minutesNapping;
    }

    public void setMinutesNapping(int minutesNapping) {
        this.minutesNapping = minutesNapping;
    }

    public int getMinutesAwakeInBed() {
        return minutesAwakeInBed;
    }

    public void setMinutesAwakeInBed(int minutesAwakeInBed) {
        this.minutesAwakeInBed = minutesAwakeInBed;
    }

    public int getMinutesAwakeNotInBed() {
        return minutesAwakeNotInBed;
    }

    public void setMinutesAwakeNotInBed(int minutesAwakeNotInBed) {
        this.minutesAwakeNotInBed = minutesAwakeNotInBed;
    }

}
