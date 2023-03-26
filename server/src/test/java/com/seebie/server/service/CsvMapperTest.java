package com.seebie.server.service;

import com.seebie.server.mapper.dtotoentity.CsvToSleepData;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CsvMapperTest {

    private CsvToSleepData fromCsv = new CsvToSleepData();

    public void testEmptyData() {

        assertThrows(IllegalArgumentException.class, () -> fromCsv.apply(TestData.createCsv(0)));
    }

    @Test
    public void testEmptyContent() {

        assertThrows(IllegalArgumentException.class, () -> fromCsv.apply(""));
    }

    @Test
    public void testMalformedCsv() {

        assertThrows(IllegalArgumentException.class, () -> fromCsv.apply("text"));
    }

    @Test
    public void testGoodCsv() {

        String rawCsv = TestData.createCsv(1);
        var parsed = fromCsv.apply(rawCsv);

        assertEquals(1, parsed.size());
    }


    @Test
    public void testNoDataFound() {

//        // should print only the header
//        String csv = service.exportCsv("someuser");
//
//        String headerRow = Arrays.asList(HEADER).stream().collect(joining(","));
//        assertEquals(headerRow + "\r\n", csv, "If no data found, entirety of csv is the header" );
    }

}
