package org.thinkbigthings.sleep;

import java.util.Collection;

public class SleepSessionAverage {

  private double averageEfficiency = 0;
  private int averageMinutesAwakeInBed = 0;
  private int averageMinutesAwakeOutOfBed = 0;
  private int averageMinutesSleeping = 0;
  private int nightsOutOfBed = 0;

  // can use this for weeks/months/etc
  public SleepSessionAverage(Collection<SleepSession> sessions) {
    for(SleepSession session : sessions) {
      averageEfficiency += session.calculateEfficiency();
      averageMinutesAwakeInBed += session.getMinutesAwakeInBed();
      averageMinutesAwakeOutOfBed += session.getMinutesAwakeOutOfBed();
      averageMinutesSleeping += session.calculateMinutesSleeping();
      nightsOutOfBed += (session.getMinutesAwakeOutOfBed() == 0) ? 0 : 1;
    }

    averageEfficiency /= sessions.size();
    averageMinutesAwakeInBed /= sessions.size();
    averageMinutesAwakeOutOfBed/= sessions.size();
    averageMinutesSleeping /= sessions.size();
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
