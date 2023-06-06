package com.seebie.server.mapper.dtotoentity;

import com.seebie.server.dto.SleepData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.util.List;
import java.util.function.Function;

import static com.seebie.server.dto.ZoneIds.AMERICA_NEW_YORK;
import static com.seebie.server.mapper.entitytodto.ZonedDateTimeConverter.parse;
import static com.seebie.server.mapper.dtotoentity.SleepDetailsToCsv.HEADER;

@Component
public class CsvToSleepData implements Function<String, List<SleepData>> {

    // need to skip header when reading
    public static final CSVFormat CSV_INPUT = CSVFormat.RFC4180.builder()
                                                                .setAllowMissingColumnNames(false)
                                                                .setSkipHeaderRecord(true)
                                                                .setHeader(HEADER.class)
                                                                .build();

    @Override
    public List<SleepData> apply(String rawCsv) {

        try {
            var records = CSV_INPUT.parse(new StringReader(rawCsv)).getRecords();

            if(records.isEmpty()) {
                throw new RuntimeException("No records were present.");
            }

            return records.stream()
                    .map(this::fromCsvRow)
                    .toList();
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Could not parse CSV input", e);
        }
    }

    private SleepData fromCsvRow(CSVRecord record) {

        var start = parse(record.get(HEADER.TIME_ASLEEP));
        var end = parse(record.get(HEADER.TIME_AWAKE));
        var minutesAwake = Integer.parseInt(record.get(HEADER.MINUTES_AWAKE));
        var notes = record.get(HEADER.NOTES);

        return new SleepData(notes, minutesAwake, start, end, AMERICA_NEW_YORK);
    }

}
