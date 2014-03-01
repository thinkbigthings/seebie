package org.thinkbigthings.sleep;

public class SleepSessionAverage {

  private double averageEfficiency = 0;
  private int averageMinutesAwakeInBed = 0;
  private int averageMinutesAwakeOutOfBed = 0;
  private int averageMinutesSleeping = 0;
  private int nightsOutOfBed = 0;
  private int numberSleepSessions = 0;

  public SleepSessionAverage with(SleepSession session) {

    numberSleepSessions++;

    averageEfficiency = ((numberSleepSessions * averageEfficiency) + session.calculateEfficiency()) / numberSleepSessions;
    averageEfficiency = ((numberSleepSessions * averageMinutesAwakeInBed) + session.getMinutesAwakeInBed()) / numberSleepSessions;
    averageEfficiency = ((numberSleepSessions * averageMinutesAwakeOutOfBed) + session.getMinutesAwakeOutOfBed()) / numberSleepSessions;
    averageEfficiency = ((numberSleepSessions * averageMinutesSleeping) + session.calculateMinutesSleeping()) / numberSleepSessions;

    nightsOutOfBed += (session.getMinutesAwakeOutOfBed() == 0) ? 0 : 1;

    return this;
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
}
