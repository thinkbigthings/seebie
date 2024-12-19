package com.seebie.server.mapper.entitytodto;

import com.seebie.server.dto.SleepData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.seebie.server.test.data.ZoneIds.AMERICA_NEW_YORK;
import static com.seebie.server.mapper.entitytodto.SleepDetailsToCsv.HEADER;
import static com.seebie.server.test.data.TestData.toSleepDetails;
import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SleepDetailsToCsvRowTest {

    private SleepDetailsToCsvRow detailsToCsvRow = new SleepDetailsToCsvRow();

    public static int count(String toSearch, String toFind) {

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

        List<String> csvRow = detailsToCsvRow.apply(toSleepDetails(data));
        assertEquals(8, count(csvRow.get(5), "\n"));

        assertEquals(HEADER.values().length, csvRow.size());
    }

    @Test
    public void testFormatWithQuotes() {

        String notesWithQuotes = "This is a note \"with quotes\" in it.";
        var data = new SleepData(notesWithQuotes, 0, now(), now(), AMERICA_NEW_YORK);

        List<String> csvRow = detailsToCsvRow.apply(toSleepDetails(data));
        assertEquals(HEADER.values().length, csvRow.size());

        String csvNotes = csvRow.get(5);
        assertEquals(2, count(csvNotes, "\""));

        assertEquals(3, count("\"\"\"", "\""));
        assertEquals(2, count("\"test\"", "\""));
        assertEquals(2, count("\"\"", "\""));
        assertEquals(1, count("\"", "\""));
    }

}
