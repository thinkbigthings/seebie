package com.seebie.server.entity;

import com.seebie.server.dto.SleepData;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.HashSet;

import static com.seebie.server.test.data.ZoneIds.AMERICA_NEW_YORK;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SleepSessionTest {

    @Test
    public void testCalculation() {

        var data = new SleepData("", 15, ZonedDateTime.now(), ZonedDateTime.now().minusHours(1), AMERICA_NEW_YORK);
        var entity = new SleepSession();
        entity.setSleepData(data.minutesAwake(), data.notes(),  data.startTime(), data.stopTime(), data.zoneId());

        assertEquals(45, entity.getMinutesAsleep());
    }
}
