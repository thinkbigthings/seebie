package org.thinkbigthings.sleep;

import org.thinkbigthings.boot.dto.SleepAveragesResource;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.thinkbigthings.boot.domain.Sleep;
import org.thinkbigthings.sleep.SleepStatisticsCalculator.Group;

public class SleepStatisticsCalculatorTest {

    private SleepStatisticsCalculator stats = new SleepStatisticsCalculator();

    private List<Sleep> januaryData = Arrays.asList(
            new Sleep("2014-01-06 05:30 AM EST", 480, 0, 0),
            new Sleep("2014-01-07 11:00 PM EST", 480, 0, 0),
            new Sleep("2014-01-08 01:30 PM EST", 480, 0, 0),
            new Sleep("2014-01-09 05:30 AM EST", 480, 0, 0),
            new Sleep("2015-01-06 05:30 AM EST", 480, 0, 0),
            new Sleep("2015-01-07 11:00 PM EST", 480, 0, 0),
            new Sleep("2015-01-08 01:30 PM EST", 480, 0, 0),
            new Sleep("2015-01-09 05:30 AM EST", 480, 0, 0),
            new Sleep("2015-01-10 05:30 AM EST", 480, 0, 0),
            new Sleep("2015-01-11 05:30 AM EST", 480, 0, 0),
            new Sleep("2015-01-12 05:30 AM EST", 480, 0, 0),
            new Sleep("2015-01-13 05:30 AM EST", 480, 0, 0),
            new Sleep("2015-01-14 05:30 AM EST", 480, 0, 0),
            new Sleep("2015-01-15 05:30 AM EST", 480, 0, 0),
            new Sleep("2015-01-16 05:30 AM EST", 480, 0, 0),
            new Sleep("2015-01-17 05:30 AM EST", 480, 0, 0),
            new Sleep("2015-01-18 05:30 AM EST", 480, 0, 0),
            new Sleep("2015-01-19 05:30 AM EST", 480, 0, 0),
            new Sleep("2015-01-20 05:30 AM EST", 480, 0, 0),
            new Sleep("2015-01-21 05:30 AM EST", 480, 0, 0),
            new Sleep("2015-01-22 05:30 AM EST", 480, 0, 0),
            new Sleep("2015-01-23 05:30 AM EST", 480, 0, 0),
            new Sleep("2015-01-24 05:30 AM EST", 480, 0, 0),
            new Sleep("2015-01-25 05:30 AM EST", 480, 0, 0),
            new Sleep("2015-01-26 05:30 AM EST", 480, 0, 0),
            new Sleep("2015-01-27 05:30 AM EST", 480, 0, 0),
            new Sleep("2015-01-28 05:30 AM EST", 480, 0, 0),
            new Sleep("2015-01-29 05:30 AM EST", 480, 0, 0)
    );

    @Test
    public void testAverageByWeek() throws Exception {
        List<? extends SleepStatistics> averages = stats.calculateAveragesByGroup(januaryData, Group.DAY);
        assertEquals(januaryData.size(), averages.size());
    }

    @Test
    public void testGroupByMonth() throws Exception {
        List<SleepAveragesResource> averages = stats.calculateAveragesByGroup(januaryData, Group.MONTH);
        assertEquals(2, averages.size());
        assertEquals("2015-01-31", averages.get(0).getGroupEnding());
        assertEquals("2014-01-31", averages.get(1).getGroupEnding());
    }

    // make sure weeks across years are actually different
    // if data for week is incomplete, should still show last day of the group
    @Test
    public void testGroupByWeek() throws Exception {
        List<SleepAveragesResource> averages = stats.calculateAveragesByGroup(januaryData, Group.WEEK);
        assertEquals(5, averages.size());

        assertEquals("2015-02-01", averages.get(0).getGroupEnding());
        assertEquals("2015-01-25", averages.get(1).getGroupEnding());
        assertEquals("2015-01-18", averages.get(2).getGroupEnding());
        assertEquals("2015-01-11", averages.get(3).getGroupEnding());
        assertEquals("2014-01-12", averages.get(4).getGroupEnding());
    }

    // make sure weeks across years are actually different
    // if data for week is incomplete, should still show last day of the group
    @Test
    public void testGroupByWeekMonday() throws Exception {
        List<SleepAveragesResource> averages = stats.calculateAveragesByGroup(januaryData, Group.WEEK_ENDING_MONDAY);
        assertEquals(5, averages.size());

        assertEquals("2015-02-02", averages.get(0).getGroupEnding());
        assertEquals("2015-01-26", averages.get(1).getGroupEnding());
        assertEquals("2015-01-19", averages.get(2).getGroupEnding());
        assertEquals("2015-01-12", averages.get(3).getGroupEnding());
        assertEquals("2014-01-13", averages.get(4).getGroupEnding());
    }

    /**
     * latest date should be the end of each year
     *
     * @throws Exception
     */
    @Test
    public void testGroupByYear() throws Exception {
        List<SleepAveragesResource> averages = stats.calculateAveragesByGroup(januaryData, Group.YEAR);
        assertEquals(2, averages.size());
        assertEquals("2015-12-31", averages.get(0).getGroupEnding());
        assertEquals("2014-12-31", averages.get(1).getGroupEnding());
        
    }

    /**
     * latest date should just be the latest data point we have.
     *
     * @throws Exception
     */
    @Test
    public void testGroupByAll() throws Exception {
        List<SleepAveragesResource> averages = stats.calculateAveragesByGroup(januaryData, Group.ALL);
        assertEquals(1, averages.size());
        assertEquals("2015-01-29", averages.get(0).getGroupEnding());
    }

}
