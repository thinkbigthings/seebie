package org.thinkbigthings.sleep;

import org.joda.time.format.DateTimeFormat;

import java.text.DecimalFormat;

public class SleepSessionFormat {

  public String date(SleepSessionAverage session) {
    String display =  "Week Ending " + DateTimeFormat.forPattern("MMMM").print(session.getLatestDateTime()) + " "
        + DateTimeFormat.forPattern("d").print(session.getLatestDateTime());
    return display;
  }

  public String summary(SleepSessionAverage session) {
    String[] sleepTime = duration(session.getAverageMinutesSleeping());
    return sleepTime[0] + "hr " + sleepTime[1] + "m";
  }

  public String date(SleepSession session) {
    String display =  DateTimeFormat.forPattern("MMMM").print(session.getFinishTime()) + " "
                    + DateTimeFormat.forPattern("d").print(session.getFinishTime()) + ", "
                    + DateTimeFormat.forPattern("EEEE").print(session.getFinishTime());
    return display;
  }

  public String summary(SleepSession session) {
    String[] sleepTime = duration(session);
    return sleepTime[0] + "hr " + sleepTime[1] + "m";
  }

  public String day(SleepSession session) {
    return DateTimeFormat.forPattern("EEEE").print(session.getFinishTime())  + " "
          + DateTimeFormat.shortDate().print(session.getFinishTime());
  }

  public String efficiency(SleepSession session) {
    DecimalFormat number = new DecimalFormat("#.#");
    return number.format(session.calculateEfficiency()*100) + "%";
  }

  public String[] duration(SleepSession session) {
    return duration(session.calculateMinutesSleeping());
  }

  public String[] duration(long minutes) {
    long min = minutes % 60;
    String minString = min < 10 ? "0" + min : String.valueOf(min);
    String hrString = String.valueOf(minutes / 60);
    return new String[]{hrString, minString};
  }
}
