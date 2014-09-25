package org.thinkbigthings.sleep;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.joda.time.DateTimeComparator;
import org.joda.time.LocalDate;

public class SleepSessionGroupings {

   // DateTimeComparator is thread-safe
   private static final DateTimeComparator DAY_COMPARATOR = DateTimeComparator.getDateOnlyInstance();
   
   public enum GroupSize {
      
      DAY(  (date)-> new LocalDate(date)),
      WEEK( (date)-> new LocalDate(date).dayOfWeek().withMaximumValue()),
      MONTH((date)-> new LocalDate(date).dayOfMonth().withMaximumValue()),
      YEAR( (date)-> new LocalDate(date).dayOfYear().withMaximumValue()),
      ALL(  (date)-> new LocalDate(Long.MAX_VALUE));

      private final Function<Date,LocalDate> keyFinder;
      
      GroupSize(Function<Date,LocalDate> lambda) {
         keyFinder = lambda;
      }
      
      public LocalDate getEndOfGroup(Date date) {
         return keyFinder.apply(date);
      }
   }

   private final List<SleepStatistics> sessions;
   
   public SleepSessionGroupings(List<SleepStatistics> allSessions) {
      sessions = new ArrayList<>(allSessions);
   }

   /**
    * 
    * @param session
    * @param from
    * @param until
    * @return true if the session's date component (i.e. ignoring time) is in 
    * the interval specified by the date components of the parameters 
    * (endpoints inclusive).
    */
   public boolean isInRange(SleepStatistics session, Date from, Date until) {
      boolean afterOrAtBeginning = DAY_COMPARATOR.compare(from, session.getTimeOutOfBed())  <= 0;
      boolean beforeOrAtEnd      = DAY_COMPARATOR.compare(session.getTimeOutOfBed(), until) <= 0;
      return afterOrAtBeginning && beforeOrAtEnd;
   }
   
   public List<SleepStatistics> calculateAveragesByGroup(Date from, Date until, GroupSize groupSize) {
      
      // TODO reduce to averages while grouping so we don't have multiple terminal operations
      // think you can use the 2-arg groupingBy() method to map each list directly to SleepAverages?
//      Map<LocalDate, List<SleepStatistics>> groups = sessions.stream()
//                                                            .filter((s)-> isInRange(s, from, until))
//                                                            .collect(Collectors.groupingBy(s -> getLatestDateForGroup(s, groupSize)));
//      return groups.values().stream().map(list -> new SleepAverages(list)).collect(Collectors.toList());
      
      return sessions.stream()
                     .filter((s)-> isInRange(s, from, until))
                     .collect(Collectors.groupingBy(s -> groupSize.getEndOfGroup(s.getTimeOutOfBed())))
                     .values()
                     .stream()
                     .map(list -> new SleepAverages(list))
                     .sorted()
                     .collect(Collectors.toList());
   }

}
