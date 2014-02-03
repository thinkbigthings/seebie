package org.thinkbigthings.seebie.android;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Minutes;

public class TimeCalculator {

  public int getMinutesBetween(int hourStart, int minuteStart, int hourEnd, int minuteEnd) {

    int absStart = (hourStart * 60) + minuteStart;
    int absEnd = (hourEnd* 60) + minuteEnd;
    if(absEnd < absStart) {
      absEnd += 60*24;
    }
    int minutes = absEnd - absStart;
    return minutes;
  }

  public Duration getDuration(DateTime earlier, DateTime later) {
    Minutes duration = Minutes.minutesBetween(earlier, later);
    return duration.toStandardDuration();
  }

}
