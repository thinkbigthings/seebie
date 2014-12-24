package org.thinkbigthings.boot.domain;

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
import org.thinkbigthings.boot.dto.SleepResource;

@Entity
public class Sleep implements SleepStatistics, Serializable, Identifiable<Long> {

   public static final long serialVersionUID = 1L;

   public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm a zzz");

   @Id
   @GeneratedValue(strategy=GenerationType.AUTO)
   protected Long id = 0L;

	/**
	 * One issue with @NotNull is that you can't cascade delete the containing entity.
	 * Cascade delete is smart enough to not violate foreign key constraints,
	 * but it can still violate not null constraints.
	 */
   @ManyToOne
   @NotNull
   protected User user;
   
   // TODO 2 would like to explicitly store timezone to eliminate need to remember this is in UTC and to ease queries
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

   // empty constructor is necessary for hibernate to create
   protected Sleep() {
       
   }
   
   public Sleep(String endStr, int mt, int mib, int mob) {
      this( DATE_TIME_FORMAT.parseDateTime(endStr).toDate(), mt, mib, mob, 0);
   }
   
   public Sleep(Date f, int t, int ib, int ob, int naps) {
      minutesTotal = t;
      timeOutOfBed = f;
      minutesAwakeInBed = ib;
      minutesAwakeNotInBed = ob;
      minutesNapping = naps;
   }

   public Sleep(User forUser, SleepResource toCopy) {
       user = forUser;
       timeOutOfBed = DATE_TIME_FORMAT.parseDateTime(toCopy.getFinishTime()).toDate();
       minutesNapping = toCopy.getMinutesNapping();
       minutesTotal = toCopy.getAllMinutes();
       minutesAwakeInBed = toCopy.getMinutesAwakeInBed();
       minutesAwakeNotInBed = toCopy.getMinutesAwakeNotInBed();
   }

    public void setTimeOutOfBed(Date timeOutOfBed) {
        this.timeOutOfBed = timeOutOfBed;
    }

    public void setMinutesNapping(int minutesNapping) {
        this.minutesNapping = minutesNapping;
    }

    public void setMinutesTotal(int minutesTotal) {
        this.minutesTotal = minutesTotal;
    }

    public void setMinutesAwakeInBed(int minutesAwakeInBed) {
        this.minutesAwakeInBed = minutesAwakeInBed;
    }

    public void setMinutesAwakeNotInBed(int minutesAwakeNotInBed) {
        this.minutesAwakeNotInBed = minutesAwakeNotInBed;
    }
    

    public int getMinutesNapping() {
        return minutesNapping;
    }

    public int getMinutesTotal() {
        return minutesTotal;
    }

    public int getMinutesAwakeInBed() {
        return minutesAwakeInBed;
    }

    public int getMinutesAwakeNotInBed() {
        return minutesAwakeNotInBed;
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
   
   public User getUser() {
       return user;
   }

}
