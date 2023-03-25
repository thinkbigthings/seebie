package com.seebie.server.mapper.entitytodto;

import com.seebie.server.dto.SleepData;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.time.ZonedDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SleepCsvMapperTest {

    private SleepDataToRow mapper = new SleepDataToRow();

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


        var data = new SleepData(newlineNotes, 0, now(), now());

        List<String> csvRow = mapper.apply(data);

        assertEquals(8, count(newlineNotes, "\n"));
        assertEquals(4, csvRow.size());
    }

    @Test
    public void testFormatWithQuotes() {

        String notesWithQuotes = "This is a note \"with quotes\" in it.";
        var data = new SleepData(notesWithQuotes, 0, now(), now());

        List<String> csvRow = mapper.apply(data);

        String csvNotes = csvRow.get(3);
        int numQuotes = count(csvNotes, "\"");

        // TODO Parameterized tests?
        int n2 = count("\"asdf\"", "\"");
        int n3 = count("\"\"", "\"");
        int n4 = count("\"", "\"");

        assertEquals(2, numQuotes);
        assertEquals(4, csvRow.size());

    }

    @Test
    public void testDateTimeFormat() {

        var data = TestData.createSleepData(1).get(0);

        List<String> csvRow = mapper.apply(data);

        assertEquals(25, csvRow.get(0).length());
        assertEquals(25, csvRow.get(1).length());

        assertEquals(4, csvRow.size());
    }
}
