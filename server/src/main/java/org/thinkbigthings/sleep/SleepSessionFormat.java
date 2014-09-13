package org.thinkbigthings.sleep;


import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class SleepSessionFormat {

  public static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
  public static final DateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a");
  
  public String week(SleepStatistics session) {
      
    String display =  "Week Ending " + DateTimeFormat.forPattern("MMMM").print(new DateTime(session.getTimeOutOfBed())) + " "
        + DateTimeFormat.forPattern("d").print(new DateTime(session.getTimeOutOfBed()));
    return display;
  }

  public String summary(SleepStatistics session) {
    String[] sleepTime = duration(session.getMinutesSleeping());
    return sleepTime[0] + "hr " + sleepTime[1] + "m";
  }

  public String day(SleepStatistics session) {
    String display =  DateTimeFormat.forPattern("MMMM").print(new DateTime(session.getTimeOutOfBed())) + " "
                    + DateTimeFormat.forPattern("d").print(new DateTime(session.getTimeOutOfBed())) + ", "
                    + DateTimeFormat.forPattern("EEEE").print(new DateTime(session.getTimeOutOfBed()));
    return display;
  }

  public String efficiency(SleepStatistics session) {
    DecimalFormat number = new DecimalFormat("#.#");
    return number.format(session.getEfficiency()*100) + "%";
  }

  public String[] duration(SleepStatistics session) {
    return duration(session.getMinutesSleeping());
  }

  public String[] duration(long minutes) {
    long min = minutes % 60;
    String minString = min < 10 ? "0" + min : String.valueOf(min);
    String hrString = String.valueOf(minutes / 60);
    return new String[]{hrString, minString};
  }
}
