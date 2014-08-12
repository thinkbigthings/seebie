package org.thinkbigthings.sleep;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class SleepSessionDaily implements SleepStatistics, Serializable {

   public static final long serialVersionUID = 1L;

   public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm a zzz");

   private final int napMinutes;
   private final int totalMinutes;
   private final Date finish;
   private final int awakeInBed;
   private final int awakeOutOfBed;

   public SleepSessionDaily(String endStr, int mt, int mib, int mob) {
      this( DATE_TIME_FORMAT.parseDateTime(endStr).toDate(), mt, mib, mob, 0);
   }

   public SleepSessionDaily(DateTime f, int t, int ib, int ob, int naps) {
      this(f.toDate(), t, ib, ob, naps);
   }
   
   public SleepSessionDaily(Date f, int t, int ib, int ob, int naps) {
      totalMinutes = t;
      finish = f;
      awakeInBed = ib;
      awakeOutOfBed = ob;
      napMinutes = naps;
   }

   /**
    * @param hour hour of day from 0-23
    * @param minute minute of hour from 0-59
    * @return a new copy
    */
   public SleepSessionDaily withStartTime(int hour, int minute) {
      validateHourAndMinute(hour, minute);
      int newTotal = Minutes.minutesBetween(new LocalTime(hour, minute), getEndAsDateTime().toLocalTime()).getMinutes();
      newTotal = newTotal < 0 ? newTotal + 1440 : newTotal;
      SleepSessionDaily session = new SleepSessionDaily(finish, newTotal, awakeInBed, awakeOutOfBed, napMinutes);
      return session;
   }

   public SleepSessionDaily withFinishDate(int year, int month, int day) {
      DateTime newFinish = getEndAsDateTime().withYear(year).withMonthOfYear(month).withDayOfMonth(day);
      SleepSessionDaily session = new SleepSessionDaily(newFinish.toDate(), totalMinutes, awakeInBed, awakeOutOfBed, napMinutes);
      return session;
   }
   
   /**
    * @param hour hour of day from 0-23napMinutes
    * @param minute minute of hour from 0-59
    * @return a new copy
    */
   public SleepSessionDaily withFinishTime(int hour, int minute) {
      validateHourAndMinute(hour, minute);
      DateTime newFinish = getEndAsDateTime().withHourOfDay(hour).withMinuteOfHour(minute);
      SleepSessionDaily session = new SleepSessionDaily(newFinish.toDate(), totalMinutes, awakeInBed, awakeOutOfBed, napMinutes);
      return session;
   }

   public SleepSessionDaily withMinutesAwakeInBed(int minutes) {
      SleepSessionDaily session = new SleepSessionDaily(finish, totalMinutes, minutes, awakeOutOfBed, napMinutes);
      return session;
   }

   public SleepSessionDaily withMinutesAwakeOutOfBed(int minutes) {
      SleepSessionDaily session = new SleepSessionDaily(finish, totalMinutes, awakeInBed, minutes, napMinutes);
      return session;
   }

   public SleepSessionDaily withNaps(int minutes) {
      SleepSessionDaily session = new SleepSessionDaily(finish, totalMinutes, awakeInBed, awakeOutOfBed, minutes);
      return session;
   }

   @Override
   public Date getTimeOutOfBed() {
      return finish;
   }
   
   /**
    * 
    * @return DateTime representing the current end Date. Assumes Date is in UTC, so DateTime is in UTC.
    */
   public DateTime getEndAsDateTime() {
      return new DateTime(finish, DateTimeZone.UTC);
   }

   /**
    *
    * @return number of minutes from start time to finish time
    */
   @Override
   public int getAllMinutes() {
      return totalMinutes;
   }

   @Override
   public int getMinutesInBed() {
      return getAllMinutes() - awakeOutOfBed;
   }

   @Override
   public int getMinutesSleeping() {
      return getMinutesInBed() - awakeInBed;
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
