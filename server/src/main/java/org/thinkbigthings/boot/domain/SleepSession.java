package org.thinkbigthings.boot.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.thinkbigthings.sleep.*;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.hateoas.Identifiable;

@Entity
@Table(name = "sleep")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SleepSession implements SleepStatistics, Serializable, Identifiable<Long> {

   public static final long serialVersionUID = 1L;

   public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm a zzz");

   @Id
   @GeneratedValue(strategy=GenerationType.AUTO)
   protected Long id = 0L;

   @Temporal(value = TemporalType.TIMESTAMP)
   protected Date timeOutOfBed = new Date();
   
   @Basic
   @NotNull
   @Min(value = 0)
   protected int minutesNapping = 0;
   
   @Basic
   @NotNull
   @Min(value = 0)
   protected int minutesTotal = 0;
   
   @Basic
   @NotNull
   @Min(value = 0)
   protected int minutesAwakeInBed = 0;
   
   @Basic
   @NotNull
   @Min(value = 0)
   protected int minutesAwakeNotInBed = 0;

   // for serialization
   protected SleepSession() {
       this(new Date(), 0, 0, 0, 0);
   }
   
   public SleepSession(String endStr, int mt, int mib, int mob) {
      this( DATE_TIME_FORMAT.parseDateTime(endStr).toDate(), mt, mib, mob, 0);
   }
   
   public SleepSession(SleepSessionJSON toCopy) {
       timeOutOfBed = DATE_TIME_FORMAT.parseDateTime(toCopy.getTimeOutOfBed()).toDate();
       minutesNapping = toCopy.getMinutesNapping();
       minutesTotal = toCopy.getMinutesTotal();
       minutesAwakeInBed = toCopy.getMinutesAwakeInBed();
       minutesAwakeNotInBed = toCopy.getMinutesAwakeNotInBed();
   }

   public SleepSession(Date f, int t, int ib, int ob, int naps) {
      minutesTotal = t;
      timeOutOfBed = f;
      minutesAwakeInBed = ib;
      minutesAwakeNotInBed = ob;
      minutesNapping = naps;
   }

   @Override
   public Date getTimeOutOfBed() {
      return timeOutOfBed;
   }
   
   /**
    * 
    * @return DateTime representing the current end Date. Assumes Date is in UTC, so DateTime is in UTC.
    */
   public DateTime getEndAsDateTime() {
      return new DateTime(timeOutOfBed, DateTimeZone.UTC);
   }

   /**
    *
    * @return number of minutes from start time to finish time
    */
   @Override
   public int getAllMinutes() {
      return minutesTotal;
   }

   // for jackson deserialization, for now...
   protected void setAllMinutes(int total) {
    minutesTotal = total;
   }
   
   @Override
   public int getMinutesInBed() {
      return getAllMinutes() - minutesAwakeNotInBed;
   }

   @Override
   public int getMinutesSleeping() {
      return getMinutesInBed() - minutesAwakeInBed;
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
   public Long getId() {
      return id;
   }

}
