package org.thinkbigthings.sleep;

import java.util.Comparator;
import org.joda.time.DateTime;

public interface SleepStatistics extends Comparable<SleepStatistics> {

   public static final Comparator<SleepStatistics> COMPARATOR = (s1, s2)-> s1.getTimeOutOfBed().compareTo(s2.getTimeOutOfBed());
   
   @Override
   default int compareTo(SleepStatistics other) {
      return COMPARATOR.compare(this, other);
   }
   
   DateTime getTimeOutOfBed();

   int getAllMinutes();

   double getEfficiency();

   int getMinutesSleeping();

   int getMinutesInBed();

}
