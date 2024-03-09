package com.seebie.server.mapper.entitytodto;

import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.Test;

import static com.seebie.server.mapper.entitytodto.SleepDetailsToCsvRowTest.count;
import static com.seebie.server.test.data.TestData.createRandomSleepData;
import static com.seebie.server.test.data.ZoneIds.AMERICA_NEW_YORK;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class SleepDetailsToCsvTest {

    private SleepDetailsToCsv detailsToCsv = new SleepDetailsToCsv();

    @Test
    public void testBasicFormat() {

        var listCount = 5;
        var list = createRandomSleepData(listCount, AMERICA_NEW_YORK).stream()
                .map(TestData::toSleepDetails)
                .toList();

        var csv = detailsToCsv.apply(list);

        // expect listCount + 1 because of the header row
        assertEquals(listCount + 1, count(csv, System.lineSeparator()));
    }
}
