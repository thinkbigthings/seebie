package org.thinkbigthings.sleep;

import java.util.*;
import org.joda.time.DateTime;

public class SleepAverages implements SleepStatistics {

   long numberSleepSessions = 0;
   private double efficiency = 0;
   private int allMinutes = 0;
   private int minutesSleeping = 0;
   private int minutesInBed = 0;
   private DateTime latestEnding = null;

   /** for serialization only */
   public SleepAverages() {

   }

   public SleepAverages(Collection<? extends SleepStatistics> set) {

      if(set.isEmpty()) {
         throw new IllegalArgumentException();
      }
      
      numberSleepSessions = set.size();

      double sumEfficiency = 0;
      int sumAllMinutes = 0;
      int sumMinutesSleeping = 0;
      int sumMinutesInBed = 0;

      for (SleepStatistics stats : set) {
         sumEfficiency += stats.getEfficiency();
         sumAllMinutes += stats.getAllMinutes();
         sumMinutesSleeping += stats.getMinutesSleeping();
         sumMinutesInBed += stats.getMinutesInBed();
         latestEnding = (latestEnding == null) ? stats.getTimeOutOfBed() : latestEnding;
         latestEnding = latestEnding.isAfter(stats.getTimeOutOfBed()) ? latestEnding : stats.getTimeOutOfBed();
      }

      efficiency = sumEfficiency / (double) numberSleepSessions;
      allMinutes = (int) Math.round((double) sumAllMinutes / (double) numberSleepSessions);
      minutesSleeping = (int) Math.round((double) sumMinutesSleeping / (double) numberSleepSessions);
      minutesInBed = (int) Math.round((double) sumMinutesInBed / (double) numberSleepSessions);
   }

   public long getCount() {
      return numberSleepSessions;
   }

   @Override
   public int getAllMinutes() {
      return allMinutes;
   }

   @Override
   public double getEfficiency() {
      return efficiency;
   }

   @Override
   public int getMinutesSleeping() {
      return minutesSleeping;
   }

   @Override
   public int getMinutesInBed() {
      return minutesInBed;
   }

   @Override
   public DateTime getTimeOutOfBed() {
      return latestEnding;
   }

}
