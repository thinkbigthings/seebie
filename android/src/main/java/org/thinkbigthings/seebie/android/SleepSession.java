package org.thinkbigthings.seebie.android;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

// TODO try out Parcelable
// http://stackoverflow.com/questions/2736389/how-to-pass-object-from-one-activity-to-another-in-android

public class SleepSession implements Serializable {

  private final int allMinutes;
  private final int minutesAwakeInBed;
  private final int minutesAwakeOutOfBed;

  public SleepSession(int allMinutes, int minutesAwakeInBed, int minutesAwakeOutOfBed) {
    this.allMinutes = allMinutes;
    this.minutesAwakeInBed = minutesAwakeInBed;
    this.minutesAwakeOutOfBed = minutesAwakeOutOfBed;
  }

  public int getAllMinutes() {
    return allMinutes;
  }

  public int getMinutesAwakeInBed() {
    return minutesAwakeInBed;
  }

  public int getMinutesAwakeOutOfBed() {
    return minutesAwakeOutOfBed;
  }

  public int calculateTotalMinutesInBed() {
    return allMinutes - minutesAwakeOutOfBed;
  }

  public int calculateMinutesInBedSleeping() {
    return calculateTotalMinutesInBed() - minutesAwakeInBed;
  }

  public double calculateEfficiency() {
    double efficiency = (double) calculateMinutesInBedSleeping() / (double) calculateTotalMinutesInBed();
    return efficiency;
  }
}
