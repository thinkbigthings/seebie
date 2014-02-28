package org.thinkbigthings.sleep;

import org.joda.time.format.DateTimeFormat;

import java.text.DecimalFormat;

public class SleepSessionFormat {

  public String date(SleepSession session) {
    String display =  DateTimeFormat.forPattern("MMMM").print(session.getFinishTime()) + " "
                    + DateTimeFormat.forPattern("d").print(session.getFinishTime()) + " "
                    + "(" + DateTimeFormat.forPattern("EEE").print(session.getFinishTime()) + ")";
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
    long time = session.calculateMinutesSleeping();
    long min = time % 60;
    String minString = min < 10 ? "0" + min : String.valueOf(min);
    String hrString = String.valueOf(time / 60);
    return new String[]{hrString, minString};
  }

}
