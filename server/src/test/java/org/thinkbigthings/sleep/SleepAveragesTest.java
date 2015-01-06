package org.thinkbigthings.sleep;


import static java.util.Arrays.asList;

import static org.junit.Assert.assertEquals;

import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.thinkbigthings.boot.dto.SleepAveragesResource;
import org.thinkbigthings.boot.dto.SleepResource;
import org.thinkbigthings.sleep.SleepStatisticsCalculator.Group;

@RunWith(Parameterized.class)
public class SleepAveragesTest {

    private final SleepAveragesResource averages;
    private final long expectedTotal;
    private final double expectedEfficiency;
    private final String expectedLatest;
    private final double delta = 0.05;

    @Parameters
    public static Collection<Object[]> createUrlUserData() throws Exception {
        
        SleepAveragesResource set2 = new SleepAveragesResource(asList(new SleepResource("2014-07-29 05:30 AM EST", 452, 40, 0),
                                                      new SleepResource("2014-07-30 05:30 AM EST", 490, 16, 50),
                                                      new SleepResource("2014-07-31 05:30 AM EST", 513, 50, 0),
                                                      new SleepResource("2014-08-01 05:30 AM EST", 524, 15, 0),
                                                      new SleepResource("2014-08-02 05:30 AM EST", 520, 40, 0),
                                                      new SleepResource("2014-08-03 05:30 AM EST", 447, 25, 0),
                                                      new SleepResource("2014-08-04 05:30 AM EST", 492, 26, 0)), Group.ALL);
        
        return Arrays.asList(new Object[][]{
            {set2, 491, 93.74, "2014-08-04"}
        });
    }

    public SleepAveragesTest(SleepAveragesResource s, long total, double efficiency, String latest) {
        averages = s;
        expectedTotal = total;
        expectedEfficiency = efficiency;
        expectedLatest = latest;
    }

    @Test
    public void testCalculateLatest() throws Exception {
        assertEquals(expectedLatest, averages.getGroupEnding());
    }

    @Test
    public void testCalculateMinutes() throws Exception {
        assertEquals(expectedTotal, averages.getAllMinutes());
    }

    @Test
    public void testEfficiency() throws Exception {
        assertEquals(expectedEfficiency, averages.getEfficiency(), delta);
    }
}
