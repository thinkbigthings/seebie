package org.thinkbigthings.seebie.android;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.io.Serializable;

// TODO try out Parcelable
// http://stackoverflow.com/questions/2736389/how-to-pass-object-from-one-activity-to-another-in-android

public class TimedSleepSession implements Serializable {

  private long minutesAwakeInBed = 0L;
  private long minutesAwakeOutOfBed = 0L;
  private DateTime startTime;
  private DateTime finishTime;

  public TimedSleepSession() {
    finishTime = new DateTime().withHourOfDay(4).withMinuteOfHour(30);
    startTime = finishTime.minusHours(7);
  }
  public TimedSleepSession withMinutesAwakeInBed(long minutes) {
    minutesAwakeInBed = minutes;
    return this;
  }
  public TimedSleepSession withMinutesAwakeOutOfBed(long minutes) {
    minutesAwakeOutOfBed = minutes;
    return this;
  }
  public TimedSleepSession withStartTime(int hour, int minute) {
    startTime.withHourOfDay(hour).withMinuteOfHour(minute);
    return this;
  }

  public TimedSleepSession withStartDate(int year, int month, int day) {
    startTime.withYear(year).withMonthOfYear(month).withDayOfMonth(day);
    return this;
  }

  public TimedSleepSession withFinishTime(int hour, int minute) {
    finishTime.withHourOfDay(hour).withMinuteOfHour(minute);
    return this;
  }

  public TimedSleepSession withFinishDate(int year, int month, int day) {
    finishTime.withYear(year).withMonthOfYear(month).withDayOfMonth(day);
    return this;
  }
  public DateTime getStartTime() {
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
    Duration duration = new Duration(startTime, finishTime);
    return duration.getStandardMinutes();
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
}
