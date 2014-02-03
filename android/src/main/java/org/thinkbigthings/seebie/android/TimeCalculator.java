package org.thinkbigthings.seebie.android;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Minutes;

public class TimeCalculator {

  public Duration getDuration(DateTime earlier, DateTime later) {
    Minutes duration = Minutes.minutesBetween(earlier, later);
    return duration.toStandardDuration();
  }

}
