package org.thinkbigthings.seebie.android;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

public class TimeCalculator {

  public int getDurationMinutes(DateTime earlier, DateTime later) {
    Minutes duration = Minutes.minutesBetween(earlier, later);
    int minutes = duration.getMinutes();
    return minutes;
  }

}
