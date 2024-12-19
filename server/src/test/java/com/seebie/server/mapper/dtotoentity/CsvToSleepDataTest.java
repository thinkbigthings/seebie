package com.seebie.server.mapper.dtotoentity;

import com.seebie.server.mapper.entitytodto.LocalDateTimeConverter;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CsvToSleepDataTest {

    private CsvToSleepData fromCsv = new CsvToSleepData();

    @Test
    public void testConstructable() {
        assertDoesNotThrow(() -> new LocalDateTimeConverter());
    }

    @Test
    public void testMalformedCsv() {

        String rawCsv = "test";

        assertThrows(IllegalArgumentException.class, () -> {
            fromCsv.apply(rawCsv);
        });

    }

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
