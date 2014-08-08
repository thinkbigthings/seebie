package org.thinkbigthings.sleep.old;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;

import java.io.Serializable;
import java.text.ParseException;
import org.joda.time.LocalDate;

// TODO does this handle daylight savings time? Leap minutes?
// if need timezone can use Joda's DateMidnight

public class SleepSession implements Serializable {

  public static final long serialVersionUID = 1L;
  
  private long id = 0L;
  private long minutesAwakeInBed = 0L;
  private long minutesAwakeOutOfBed = 0L;
  private LocalTime startTime;
  private LocalTime finishTime;
  private LocalDate finishDate = new LocalDate();

  public SleepSession() {
    finishTime = new LocalTime().withHourOfDay(4).withMinuteOfHour(30);
    startTime = finishTime.minusMinutes(420);
  }

  public static SleepSession fromIntData(int startHr, int startMin, int endHr, int endMin, int awakeIn, int awakeOut) throws ParseException {
      SleepSession session = new SleepSession();
      session.withStartTime(startHr, startMin);
      session.withFinishTime(endHr,   endMin);
      session.withMinutesAwakeInBed(awakeIn);
      session.withMinutesAwakeOutOfBed(awakeOut);
      return session;
  }
  
  public static SleepSession fromLongData(Long inId, Long finish, Long durationMinutes, Long minutesAwakeIn, Long minutesOut) {
      SleepSession session = new SleepSession();
      session.id = inId;
      session.finishTime = new DateTime(finish).toLocalTime();
      session.finishDate = new DateTime(finish).toLocalDate();
      session.startTime = session.finishTime.minusMinutes(durationMinutes.intValue());
      session.minutesAwakeInBed = minutesAwakeIn;
      session.minutesAwakeOutOfBed = minutesOut;
      return session;
  }
  
  public SleepSession(SleepSession toCopy) {
    id = toCopy.id;
    minutesAwakeInBed = toCopy.minutesAwakeInBed;
    minutesAwakeOutOfBed = toCopy.minutesAwakeOutOfBed;
    startTime = toCopy.startTime;
    finishTime = toCopy.finishTime;
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
    finishDate = finishDate.withYear(year).withMonthOfYear(month).withDayOfMonth(day);
    return this;
  }
  public LocalTime getStartTime() {
    return startTime;
  }
  public LocalTime getFinishTime() {
    return finishTime;
  }
  public LocalDate getFinishDate() {
    return finishDate;
  }
  public long getMinutesAwakeInBed() {
    return minutesAwakeInBed;
  }

  public long getMinutesAwakeOutOfBed() {
    return minutesAwakeOutOfBed;
  }
  
  /**
   * 
   * @return number of minutes from start time to finish time 
   */

  public long calculateAllMinutes() {
    long totalMinutes = Minutes.minutesBetween(startTime, finishTime).getMinutes();
    totalMinutes = totalMinutes < 0 ? totalMinutes + 1440 : totalMinutes;
    return totalMinutes;
  }

  public long calculateTotalMinutesInBed() {
    return calculateAllMinutes() - minutesAwakeOutOfBed;
  }


  public long calculateMinutesSleeping() {
    return calculateTotalMinutesInBed() - minutesAwakeInBed;
  }


  public double calculateEfficiency() {
    return (double) calculateMinutesSleeping() / (double) calculateTotalMinutesInBed();
  }

  public Long getId() {
    return id;
  }

}
