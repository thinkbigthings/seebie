package org.thinkbigthings.sleep.old;

import org.joda.time.LocalDate;

public class SleepSessionAverage {

  private long id = 0;
  private double averageEfficiency = 0;
  private int averageMinutesAwakeInBed = 0;
  private int averageMinutesAwakeOutOfBed = 0;
  private int averageMinutesSleeping = 0;
  private int nightsOutOfBed = 0;
  private int numberSleepSessions = 0;
  private LocalDate latestDate = null;

  public SleepSessionAverage() {

  }

  public SleepSessionAverage with(SleepSession session) {

    int oldNumber = numberSleepSessions;
    int newNumber = numberSleepSessions + 1;

    // TODO round these to nearest int instead of casting
    averageEfficiency = ((oldNumber * averageEfficiency) + session.calculateEfficiency()) / newNumber;
    averageMinutesAwakeInBed = (int) (((oldNumber * averageMinutesAwakeInBed) + session.getMinutesAwakeInBed()) / newNumber);
    averageMinutesAwakeOutOfBed = (int) ((oldNumber * averageMinutesAwakeOutOfBed) + session.getMinutesAwakeOutOfBed()) / newNumber;
    averageMinutesSleeping = (int) (((oldNumber * averageMinutesSleeping) + session.calculateMinutesSleeping()) / newNumber);
    numberSleepSessions = newNumber;
    nightsOutOfBed += (session.getMinutesAwakeOutOfBed() == 0) ? 0 : 1;

    boolean isCurrentSessionLatest = latestDate == null || session.getFinishDate().isAfter(latestDate);
    latestDate = isCurrentSessionLatest ? session.getFinishDate() : latestDate;
    id = isCurrentSessionLatest ? session.getId() : id;

    return this;
  }

  public long getId() {
    return id;
  }

  public double getAverageEfficiency() {
    return averageEfficiency;
  }

  public int getAverageMinutesAwakeInBed() {
    return averageMinutesAwakeInBed;
  }

  public int getAverageMinutesAwakeOutOfBed() {
    return averageMinutesAwakeOutOfBed;
  }

  public int getAverageMinutesSleeping() {
    return averageMinutesSleeping;
  }

  public int getNightsOutOfBed() {
    return nightsOutOfBed;
  }

  public LocalDate getLatestDate() {
    return latestDate;
  }
}
