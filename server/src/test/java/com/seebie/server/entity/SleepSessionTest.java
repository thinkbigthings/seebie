package com.seebie.server.entity;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.HashSet;

import static com.seebie.server.dto.ZoneIds.AMERICA_NEW_YORK;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SleepSessionTest {

    @Test
    public void testCalculation() {

        var entity = new SleepSession();
        entity.setSleepData(15, "", new HashSet<>(), ZonedDateTime.now(), ZonedDateTime.now().minusHours(1), AMERICA_NEW_YORK);

        assertEquals(45, entity.getMinutesAsleep());
    }
}
