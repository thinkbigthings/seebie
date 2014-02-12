package org.thinkbigthings.seebie.android;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;

import java.io.Serializable;

// TODO try out Parcelable
// http://stackoverflow.com/questions/2736389/how-to-pass-object-from-one-activity-to-another-in-android

// TODO does this handle daylight savings time? Should I be storing the startTime as a DateTime?

public class SleepSession implements Serializable {

  private long id = 0L;
  private long minutesAwakeInBed = 0L;
  private long minutesAwakeOutOfBed = 0L;
  private LocalTime startTime;
  private DateTime finishTime;

  public SleepSession() {
    finishTime = new DateTime().withHourOfDay(4).withMinuteOfHour(30);
    startTime = finishTime.minusMinutes(420).toLocalTime();
  }
  public SleepSession(Long inId, Long finish, Long durationMinutes, Long minutesAwakeIn, Long minutesOut) {
    id = inId;
    finishTime = new DateTime(finish);
    startTime = finishTime.minusMinutes(durationMinutes.intValue()).toLocalTime();
    minutesAwakeInBed = minutesAwakeIn;
    minutesAwakeOutOfBed = minutesOut;
  }
  public SleepSession withMinutesAwakeInBed(long minutes) {
    minutesAwakeInBed = minutes;
    return this;
  }
  public SleepSession withMinutesAwakeOutOfBed(long minutes) {
    minutesAwakeOutOfBed = minutes;
    return this;
  }
  public SleepSession withStartTime(int hour, int minute) {
    startTime = startTime.withHourOfDay(hour).withMinuteOfHour(minute);
    return this;
  }

  public SleepSession withFinishTime(int hour, int minute) {
    finishTime = finishTime.withHourOfDay(hour).withMinuteOfHour(minute);
    return this;
  }

  public SleepSession withFinishDate(int year, int month, int day) {
    finishTime = finishTime.withYear(year).withMonthOfYear(month).withDayOfMonth(day);
    return this;
  }
  public LocalTime getStartTime() {
    return startTime;
  }
  public DateTime getFinishTime() {
    return finishTime;
  }
  public long getMinutesAwakeInBed() {
    return minutesAwakeInBed;
  }

  public long getMinutesAwakeOutOfBed() {
    return minutesAwakeOutOfBed;
  }

  public long calculateAllMinutes() {
    long totalMinutes = Minutes.minutesBetween(startTime, finishTime.toLocalTime()).getMinutes();
    totalMinutes += (60*24);
    return totalMinutes;
  }
  public long calculateTotalMinutesInBed() {
    return calculateAllMinutes() - minutesAwakeOutOfBed;
  }

  public long calculateMinutesInBedSleeping() {
    return calculateTotalMinutesInBed() - minutesAwakeInBed;
  }

  public double calculateEfficiency() {
    double efficiency = (double) calculateMinutesInBedSleeping() / (double) calculateTotalMinutesInBed();
    return efficiency;
  }

  public Long getId() {
    return id;
  }
}
