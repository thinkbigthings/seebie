package com.seebie.server.mapper.dtotoentity;

import com.seebie.server.mapper.dtotoentity.CsvToSleepData;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvToSleepDataTest {

    private CsvToSleepData fromCsv = new CsvToSleepData();

    @Test
    public void testEmptyCsv() {

        String rawCsv = TestData.createCsv(0);
        var parsed = fromCsv.apply(rawCsv);

        assertEquals(0, parsed.size());
    }

    @Test
    public void testGoodCsv() {

        String rawCsv = TestData.createCsv(1);
        var parsed = fromCsv.apply(rawCsv);

        assertEquals(1, parsed.size());
    }

}
