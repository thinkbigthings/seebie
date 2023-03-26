package com.seebie.server.mapper.dtotoentity;

import com.seebie.server.dto.SleepData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.time.Duration;
import java.util.List;
import java.util.function.Function;

import static com.seebie.server.mapper.entitytodto.ZonedDateTimeToString.format;
import static com.seebie.server.mapper.entitytodto.ZonedDateTimeToString.parse;
import static com.seebie.server.service.SleepService.HEADER;

@Component
public class CsvToSleepData implements Function<String, List<SleepData>> {

    public static final CSVFormat CSV = CSVFormat.DEFAULT.builder().setHeader(HEADER).build();

    // need to skip header when reading
    public static final CSVFormat CSV_INPUT = CSVFormat.RFC4180.builder()
                                                                .setAllowMissingColumnNames(false)
                                                                .setSkipHeaderRecord(true)
                                                                .setHeader(HEADER)
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

    public SleepData fromCsvRow(CSVRecord record) {

        // TODO maybe use headers with enums
        //  {"Time-Asleep","Time-Awake","Duration-Minutes","Num-Times-Up","Notes"};

        var start = parse(record.get(HEADER[0]));
        var end = parse(record.get(HEADER[1]));
        var numTimesUp = Integer.parseInt(record.get(HEADER[3]));
        var notes = record.get(HEADER[4]);

        return new SleepData(notes, numTimesUp, start, end);
    }

    public static List<String> toCsvRow(SleepData data) {

        return List.of(
                format(data.startTime()),
                format(data.stopTime()),
                Long.toString(Duration.between(data.startTime(), data.stopTime()).toMinutes()),
                Integer.toString(data.outOfBed()),
                data.notes()
        );
    }
}
