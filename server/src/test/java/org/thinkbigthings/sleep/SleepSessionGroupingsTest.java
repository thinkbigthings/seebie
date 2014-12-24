package org.thinkbigthings.sleep;

import org.thinkbigthings.boot.assembler.SleepSessionDaily;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import static org.junit.Assert.*;

import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.thinkbigthings.boot.dto.SleepResource;
import org.thinkbigthings.sleep.SleepSessionGroupings.GroupSize;

public class SleepSessionGroupingsTest {

   private SleepSessionGroupings groups;
   private List<SleepStatistics> allData = new ArrayList<>();
      DateTimeFormatter format = SleepResource.DATE_TIME_FORMAT;
      Date firstRecordedDay;
      Date lastRecordedDay;
   
   @Before
   public void setup() {
      
      DateTime first = format.parseDateTime("2014-12-23 05:30 AM EST");
      firstRecordedDay = first.toDate();
      
      for (int i = 0; i < 10; i++) {
         DateTime date = first.plusDays(i);
         lastRecordedDay = date.toDate();
         allData.add(new SleepSessionDaily(lastRecordedDay, 480, 0, 0, 0));
      }
      
      groups = new SleepSessionGroupings(allData);
   }
   
   @Test
   public void testRanges() throws Exception {
      SleepStatistics daily = new SleepSessionDaily("2014-12-23 05:30 AM EST", 480, 0, 0);
      
      // outside lower
      assertFalse(groups.isInRange(daily, format.parseDateTime("2014-12-20 05:30 AM EST").toDate(), format.parseDateTime("2014-12-21 05:30 AM EST").toDate()));
      
      // on lower edge, middle, upper edge
      assertTrue(groups.isInRange(daily, format.parseDateTime("2014-12-23 05:30 AM EST").toDate(), format.parseDateTime("2014-12-25 05:30 AM EST").toDate()));
      assertTrue(groups.isInRange(daily, format.parseDateTime("2014-12-22 05:30 AM EST").toDate(), format.parseDateTime("2014-12-25 05:30 AM EST").toDate()));
      assertTrue(groups.isInRange(daily, format.parseDateTime("2014-12-20 05:30 AM EST").toDate(), format.parseDateTime("2014-12-23 05:30 AM EST").toDate()));
      
      // outside upper
      assertFalse(groups.isInRange(daily, format.parseDateTime("2014-12-24 05:30 AM EST").toDate(), format.parseDateTime("2014-12-25 05:30 AM EST").toDate()));
   }

   @Test
   public void testCalculateAveragesByDay() {

      List<SleepStatistics> stats = groups.calculateAveragesByGroup(firstRecordedDay, lastRecordedDay, GroupSize.DAY);
      assertEquals(allData.size(), stats.size());
   }
   
   @Test
   public void testCalculateAveragesByWeek() {
      List<SleepStatistics> stats = groups.calculateAveragesByGroup(firstRecordedDay, lastRecordedDay, GroupSize.WEEK);
      assertEquals(2, stats.size());
   }
   
   @Test
   public void testCalculateAveragesByMonth() {
      List<SleepStatistics> stats = groups.calculateAveragesByGroup(firstRecordedDay, lastRecordedDay, GroupSize.MONTH);
      assertEquals(2, stats.size());
   }
   
   @Test
   public void testCalculateAveragesByYear() {
      List<SleepStatistics> stats = groups.calculateAveragesByGroup(firstRecordedDay, lastRecordedDay, GroupSize.YEAR);
      assertEquals(2, stats.size());
   }
   
   @Test
   public void testCalculateAveragesByAll() {
      List<SleepStatistics> stats = groups.calculateAveragesByGroup(firstRecordedDay, lastRecordedDay, GroupSize.ALL);
      assertEquals(1, stats.size());
   }

}
