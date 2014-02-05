package org.thinkbigthings.seebie.android;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Minutes;

public class TimeCalculator {

  public int getMinutesBetween(int hourStart, int minuteStart, int hourEnd, int minuteEnd) {

    if(hourStart < 0 || 23 < hourStart) {
      throw new IllegalArgumentException("must be 0-23: " + hourStart);
    }
    if(hourEnd < 0 || 23 < hourEnd) {
      throw new IllegalArgumentException("must be 0-23: " + hourEnd);
    }
    if(minuteStart < 0 || 59 < minuteStart) {
      throw new IllegalArgumentException("must be 0-59: " + minuteStart);
    }
    if(minuteEnd < 0 || 59 < minuteEnd) {
      throw new IllegalArgumentException("must be 0-59: " + minuteEnd);
    }

    // TODO use LocalTime(int hourOfDay, int minuteOfHour) for calculator?
    int absStart = (hourStart * 60) + minuteStart;
    int absEnd = (hourEnd* 60) + minuteEnd;
    if(absEnd < absStart) {
      absEnd += 60*24;
    }
    int minutes = absEnd - absStart;
    return minutes;
  }

  public Duration getDuration(DateTime earlier, DateTime later) {
    earlier.toLocalTime();
    Minutes duration = Minutes.minutesBetween(earlier, later);
    return duration.toStandardDuration();
  }

}
