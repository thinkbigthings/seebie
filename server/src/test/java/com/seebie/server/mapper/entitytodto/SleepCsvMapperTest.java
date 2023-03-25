package com.seebie.server.mapper.entitytodto;

import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SleepCsvMapperTest {

    private SleepCsvMapper mapper = new SleepCsvMapper();

    @Test
    public void testFormat() {

        var data = TestData.createSleepData(1).get(0);

        String[] csvRow = mapper.apply(data);

        assertEquals(25, csvRow[0].length());
        assertEquals(25, csvRow[1].length());

        assertEquals(4, csvRow.length);
    }
}
