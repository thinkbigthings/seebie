package org.thinkbigthings.sleep;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.thinkbigthings.boot.domain.Sleep;
import org.thinkbigthings.boot.dto.SleepAveragesResource;

public class SleepStatisticsCalculator {

    public enum Group {

        DAY((date)                -> date.toLocalDate()),
        WEEK((date)               -> date.dayOfWeek().withMaximumValue().toLocalDate()),
        WEEK_ENDING_SUNDAY((date) -> date.dayOfWeek().withMaximumValue().toLocalDate()),
        WEEK_ENDING_MONDAY((date) -> date.dayOfWeek().withMaximumValue().plusDays(1).toLocalDate()),
        WEEK_ENDING_TUESDAY((date)-> date.dayOfWeek().withMaximumValue().plusDays(2).toLocalDate()),
        MONTH((date)              -> date.dayOfMonth().withMaximumValue().toLocalDate()),
        YEAR((date)               -> date.dayOfYear().withMaximumValue().toLocalDate()),
        ALL((date)                -> Constants.groupAll);

        private final static class Constants {
            /** just for grouping, not representative of dates in the set */
            public static final LocalDate groupAll = new LocalDate(0L);
        }
        
        private final Function<DateTime, LocalDate> keyFinder;
        
        Group(Function<DateTime, LocalDate> lambda) {
            keyFinder = lambda;
        }

        public LocalDate getEndOfGroup(DateTime date) {
            return keyFinder.apply(date);
        }
    }

    public SleepStatisticsCalculator() {

    }

    public List<SleepAveragesResource> calculateAveragesByGroup(List<Sleep> sessions, Group group) {
        return calculateAveragesByGroup(sessions, group, SleepStatistics.BY_TIME_DESCENDING);
    }
    
    public List<SleepAveragesResource> calculateAveragesByGroup(List<Sleep> sessions, Group group, final Comparator<? super SleepAveragesResource> comparator) {

        // TODO 5 reduce to averages while grouping so we don't have multiple terminal operations
        // also could limit by the elements being requested for a page
        // maybe use map/reduce/limit? can we do a filter for what will be in the page before any calculations?
        // can we eliminate all this with a clever db query?
        
        return sessions.stream()
                .collect(Collectors.groupingBy(s -> group.getEndOfGroup(s.getTimeOutOfBed())))
                .values()
                .stream()
                .map(list -> new SleepAveragesResource(list, group))
                .sorted(comparator)
                .collect(Collectors.toList());        
    }
    


}
