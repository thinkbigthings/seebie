package org.thinkbigthings.sleep;

import java.io.Serializable;
import java.util.Date;
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

   // for serialization
   protected SleepSessionDaily() {
       this(new Date(), 0, 0, 0, 0);
   }
   
   // TODO deserialize with constructor instead of setters?
   // http://www.cowtowncoder.com/blog/archives/2010/08/entry_409.html
   // http://stackoverflow.com/questions/15121643/immutable-polymorphic-pojo-json-serialization-with-jackson
   // http://jira.codehaus.org/browse/JACKSON-469
   public SleepSessionDaily(String endStr, int mt, int mib, int mob) {
      this( DATE_TIME_FORMAT.parseDateTime(endStr).toDate(), mt, mib, mob, 0);
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
    * @param hour hour of day from 0-23
    * @param minute minute of hour from 0-59
    * @return a new copy
    */
   public SleepSessionDaily withFinishTime(int hour, int minute) {
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

}
