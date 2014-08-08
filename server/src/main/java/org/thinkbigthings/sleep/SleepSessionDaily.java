package org.thinkbigthings.sleep;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class SleepSessionDaily implements SleepStatistics, Serializable {

   public static final long serialVersionUID = 1L;

   public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm a zzz");

   private final Minutes naps;
   private final Minutes total;
   private final DateTime finish;
   private final Minutes awakeInBed;
   private final Minutes awakeOutOfBed;

   public SleepSessionDaily(String endStr, int mt, int mib, int mob) {
      this( DATE_TIME_FORMAT.parseDateTime(endStr),
            Minutes.minutes(mt),
            Minutes.minutes(mib),
            Minutes.minutes(mob),
            Minutes.ZERO);
   }

   public SleepSessionDaily(DateTime f, Minutes t, Minutes ib, Minutes ob, Minutes n) {
      total = t;
      finish = f;
      awakeInBed = ib;
      awakeOutOfBed = ob;
      naps = n;
   }

   /**
    * @param hour hour of day from 0-23
    * @param minute minute of hour from 0-59
    * @return a new copy
    */
   public SleepSessionDaily withStartTime(int hour, int minute) {
      validateHourAndMinute(hour, minute);
      int newTotal = Minutes.minutesBetween(new LocalTime(hour, minute), finish.toLocalTime()).getMinutes();
      newTotal = newTotal < 0 ? newTotal + 1440 : newTotal;
      SleepSessionDaily session = new SleepSessionDaily(finish, Minutes.minutes(newTotal), awakeInBed, awakeOutOfBed, naps);
      return session;
   }

   public SleepSessionDaily withFinishDate(int year, int month, int day) {
      DateTime newFinish = finish.withYear(year).withMonthOfYear(month).withDayOfMonth(day);
      SleepSessionDaily session = new SleepSessionDaily(newFinish, total, awakeInBed, awakeOutOfBed, naps);
      return session;
   }

   /**
    * @param hour hour of day from 0-23
    * @param minute minute of hour from 0-59
    * @return a new copy
    */
   public SleepSessionDaily withFinishTime(int hour, int minute) {
      validateHourAndMinute(hour, minute);
      DateTime newFinish = finish.withHourOfDay(hour).withMinuteOfHour(minute);
      SleepSessionDaily session = new SleepSessionDaily(newFinish, total, awakeInBed, awakeOutOfBed, naps);
      return session;
   }

   public SleepSessionDaily withMinutesAwakeInBed(int m) {
      SleepSessionDaily session = new SleepSessionDaily(finish, total, Minutes.minutes(m), awakeOutOfBed, naps);
      return session;
   }

   public SleepSessionDaily withMinutesAwakeOutOfBed(int m) {
      SleepSessionDaily session = new SleepSessionDaily(finish, total, awakeInBed, Minutes.minutes(m), naps);
      return session;
   }

   public SleepSessionDaily withNaps(int minutes) {
      SleepSessionDaily session = new SleepSessionDaily(finish, total, awakeInBed, awakeOutOfBed, Minutes.minutes(minutes));
      return session;
   }

   @Override
   public Date getEnd() {
      return finish.toDate();
   }

   /**
    *
    * @return number of minutes from start time to finish time
    */
   @Override
   public long getAllMinutes() {
      return total.getMinutes();
   }

   @Override
   public long getMinutesInBed() {
      return getAllMinutes() - awakeOutOfBed.getMinutes();
   }

   @Override
   public long getMinutesSleeping() {
      return getMinutesInBed() - awakeInBed.getMinutes();
   }

   /**
    * 
    * @return a decimal number between 0 and 100 representing the sleep efficiency as a percentage.
    */
   @Override
   public double getEfficiency() {
      return 100 * (double) getMinutesSleeping() / (double) getMinutesInBed();
   }

   @Override
   public int hashCode() {
      int hash = 5;
      hash = 59 * hash + Objects.hashCode(this.finish);
      return hash;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final SleepSessionDaily other = (SleepSessionDaily) obj;

      return this.finish.equals(other.finish);
   }

   private void validateHourAndMinute(int hour, int minute) {
      if(hour < 0 || 23 < hour) {
         throw new IllegalArgumentException(Integer.toString(hour));
      }
      if(minute < 0 || 59 < minute) {
         throw new IllegalArgumentException(Integer.toString(hour));
      }   }

}
