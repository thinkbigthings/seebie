package org.thinkbigthings.sleep;

import java.util.Comparator;
import java.util.Date;

public interface SleepStatistics extends Comparable<SleepStatistics> {

   public static final Comparator<SleepStatistics> COMPARATOR = (s1, s2)-> s1.getEnd().compareTo(s2.getEnd());
   
   @Override
   default int compareTo(SleepStatistics other) {
      return COMPARATOR.compare(this, other);
   }
   
   Date getEnd();

   long getAllMinutes();

   double getEfficiency();

   long getMinutesSleeping();

   long getMinutesInBed();

}
