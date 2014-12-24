package org.thinkbigthings.sleep;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.thinkbigthings.boot.dto.SleepResource;

@RunWith(Parameterized.class)
public class SleepResourceTest {

    private final SleepResource session;
    private final long expectedTotal;
    private final double expectedEfficiency;
    private final double delta = 0.01;

    @Parameters
    public static Collection<Object[]> createTestData() throws Exception {
        return Arrays.asList(new Object[][]{
            {new SleepResource("2014-07-03 05:30 AM EST", 480, 0, 0), 480, 100},
            {new SleepResource("2014-07-02 11:00 PM EST", 90, 0, 0), 90, 100},
            {new SleepResource("2014-07-03 01:30 PM EST", 60, 0, 0), 60, 100},
            {new SleepResource("2014-07-03 05:30 AM EST", 480, 35, 20), 480, 92.4},
            {new SleepResource("2014-07-03 05:30 AM EST", 480, 35, 0), 480, 92.7}
        });
    }

    public SleepResourceTest(SleepResource s, long total, double efficiency) {
        session = s;
        expectedTotal = total;
        expectedEfficiency = efficiency;
    }

    @Test
    public void testChangeStart() throws Exception {
        // what if finish time is 1PM and they want to switch the start from 11AM that morning to 11PM the night before...
        assertEquals(2 * 60, session.setFinishTime(13, 5).setStartTime(11, 5).getAllMinutes());
        assertEquals(14 * 60, session.setFinishTime(13, 5).setStartTime(23, 5).getAllMinutes());
    }

    @Test
    public void testTimeZones() throws Exception {

        SleepResource s1 = new SleepResource("2014-09-02 07:00 AM EST", 420, 0, 0);
        SleepResource s2 = new SleepResource("2014-09-02 07:00 AM PST", 420, 0, 0);

        Assert.assertNotEquals(s1.getTimeOutOfBed(), s2.getTimeOutOfBed());
    }

    @Test
    public void testCalculateMinutes() throws Exception {
        assertEquals(expectedTotal, session.getAllMinutes());
    }

    @Test
    public void testEfficiency() throws Exception {
        assertEquals(expectedEfficiency, session.getEfficiency(), delta);
    }
}
