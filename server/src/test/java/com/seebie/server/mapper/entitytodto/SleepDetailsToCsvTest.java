package com.seebie.server.mapper.entitytodto;

import com.seebie.server.dto.SleepData;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static com.seebie.server.test.data.ZoneIds.AMERICA_NEW_YORK;
import static com.seebie.server.mapper.dtotoentity.SleepDetailsToCsv.HEADER;
import static com.seebie.server.test.data.TestData.createRandomSleepData;
import static com.seebie.server.test.data.TestData.toSleepDetails;
import static java.time.ZonedDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SleepDetailsToCsvTest {

    private SleepDetailsToCsvRow detailsToCsv = new SleepDetailsToCsvRow();

    private int count(String toSearch, String toFind) {
        int index = toSearch.indexOf(toFind);
        int count = 0;
        while(index >= 0 ) {
            count++;
            index = toSearch.indexOf(toFind, index+1);
        }
        return count;
    }

    @Test
    public void testFormatWithNewlines() {

        var newlineNotes = """
                I slept ok.
                        Not "great."
                Trying to use the sunrise alarm again...
                        
                FTW !
                        ""
                        
                        
                """;


        var data = new SleepData(newlineNotes, 0, now(), now(), AMERICA_NEW_YORK);

        List<String> csvRow = detailsToCsv.apply(toSleepDetails(data));
        assertEquals(8, count(csvRow.get(5), "\n"));

        assertEquals(HEADER.values().length, csvRow.size());
    }

    @Test
    public void testFormatWithQuotes() {

        String notesWithQuotes = "This is a note \"with quotes\" in it.";
        var data = new SleepData(notesWithQuotes, 0, now(), now(), AMERICA_NEW_YORK);

        List<String> csvRow = detailsToCsv.apply(toSleepDetails(data));
        assertEquals(HEADER.values().length, csvRow.size());

        String csvNotes = csvRow.get(5);
        assertEquals(2, count(csvNotes, "\""));

        assertEquals(3, count("\"\"\"", "\""));
        assertEquals(2, count("\"asdf\"", "\""));
        assertEquals(2, count("\"\"", "\""));
        assertEquals(1, count("\"", "\""));
    }

    @Disabled("Fails on Github, need to upload test output to be able to investigate")
    @Test
    public void testDateTimeFormat() {

        var data = createRandomSleepData(ZonedDateTime.now(), AMERICA_NEW_YORK);

        List<String> csvRow = detailsToCsv.apply(toSleepDetails(data));

        // TODO this breaks the build but works locally
        // it's not a "great" test but I do expect the format to be consistent across locales.
        //2023-03-25T13:44:00-04:00

        assertEquals(25, csvRow.get(0).length(), "Expected 25 characters, got: " + csvRow.get(0));
        assertEquals(25, csvRow.get(1).length(), "Expected 25 characters, got: " + csvRow.get(1));

        assertEquals(HEADER.values().length, csvRow.size());
    }
}