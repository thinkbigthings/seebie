package org.thinkbigthings.seebie.android;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Minutes;

public class TimeCalculator {

  public int getMinutesBetween(int hourStart, int minuteStart, int hourEnd, int minuteEnd) {

    if(0 > hourStart || hourStart > 23) {
      throw new IllegalArgumentException("must be 0-23: " + hourStart);
    }
    if(0 > hourEnd || hourEnd > 23) {
      throw new IllegalArgumentException("must be 0-23: " + hourEnd);
    }
    if(0 > minuteStart || minuteStart > 59) {
      throw new IllegalArgumentException("must be 0-59: " + minuteStart);
    }
    if(0 > minuteEnd || minuteEnd > 59) {
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
