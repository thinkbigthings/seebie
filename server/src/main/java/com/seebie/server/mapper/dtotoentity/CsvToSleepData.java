package com.seebie.server.mapper.dtotoentity;

import com.seebie.server.dto.SleepData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static com.seebie.server.mapper.entitytodto.ZonedDateTimeConverter.parse;
import static com.seebie.server.mapper.entitytodto.SleepDetailsToCsv.HEADER;

@Component
public class CsvToSleepData implements Function<String, List<SleepData>> {

    // allowMissingColumnNames is only used when parsing
    public static final CSVFormat CSV_INPUT = CSVFormat.RFC4180.builder()
                                                                .setAllowMissingColumnNames(false)
                                                                .setSkipHeaderRecord(true)
                                                                .setHeader(HEADER.class)
                                                                .build();

    public static IllegalArgumentException missingHeader() {
        return new IllegalArgumentException("CSV input does not contain all required columns");
    }

    @Override
    public List<SleepData> apply(String rawCsv) {

        if(HEADER.values().length != Arrays.stream(HEADER.values()).map(Enum::name).filter(rawCsv::contains).count()) {
            throw missingHeader();
        }

        try {
            var list = CSV_INPUT.parse(new StringReader(rawCsv))
                    .stream()
                    .map(this::fromCsvRow)
                    .toList();
            return list;
        }
        catch (IOException | IllegalStateException e) {
            throw new IllegalArgumentException("Could not parse CSV input", e);
        }
    }

    private SleepData fromCsvRow(CSVRecord record) {

        var start = parse(record.get(HEADER.TIME_ASLEEP));
        var end = parse(record.get(HEADER.TIME_AWAKE));
        var zoneId = record.get(HEADER.TIMEZONE);
        var minutesAwake = Integer.parseInt(record.get(HEADER.MINUTES_AWAKE));
        var notes = record.get(HEADER.NOTES);

        return new SleepData(notes, minutesAwake, start, end, zoneId);
    }
}
