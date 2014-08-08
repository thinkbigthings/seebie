package org.thinkbigthings.sleep.old;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SleepSessionTest {

    private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
    
    private final SleepSession session;
    private final long expectedTotal;
    private final double expectedEfficiency;
    private final double delta = 0.005;
    
    @Parameters
    public static Collection<Object[]> createUrlUserData() throws Exception {
        Long ID = 1L;
        return Arrays.asList(new Object[][]{
            { SleepSession.fromIntData(21, 30, 4, 30, 0, 0), 420L, 1.0d},
            { SleepSession.fromLongData(ID, format.parse("2014-07-02 05:30 AM").getTime(), 420L, 25L, 0L), 420L, 0.94 },
            { SleepSession.fromLongData(ID, format.parse("2014-07-02 05:30 AM").getTime(), 150L, 25L, 0L), 150L, 0.83 },
            { SleepSession.fromLongData(ID, format.parse("2014-07-02 12:30 PM").getTime(), 60L, 25L, 0L), 60L, 0.58 }
        });
    }

    public SleepSessionTest(SleepSession s, long total, double efficiency) {
        session = s;
        expectedTotal = total;
        expectedEfficiency = efficiency;
    }

    @Test
    public void testCalculateMinutes() throws Exception {
        assertEquals(expectedTotal, session.calculateAllMinutes());
    }

    @Test
    public void testEfficiency() throws Exception {
        assertEquals(expectedEfficiency, session.calculateEfficiency(), delta);
    }
    

}
