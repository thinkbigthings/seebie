package org.thinkbigthings.sleep;

import java.util.Comparator;
import org.joda.time.DateTime;

public interface SleepStatistics extends Comparable<SleepStatistics> {

   public static final Comparator<SleepStatistics> BY_TIME_ASCENDING = (s1, s2)-> s1.getTimeOutOfBed().compareTo(s2.getTimeOutOfBed());
   public static final Comparator<SleepStatistics> BY_TIME_DESCENDING = (s1, s2)-> s2.getTimeOutOfBed().compareTo(s1.getTimeOutOfBed());
   
   @Override
   default int compareTo(SleepStatistics other) {
      return BY_TIME_DESCENDING.compare(this, other);
   }
   
   DateTime getTimeOutOfBed();

   int getAllMinutes();

   double getEfficiency();

   int getMinutesSleeping();

   int getMinutesInBed();
   
   int getMinutesNapping();

}
