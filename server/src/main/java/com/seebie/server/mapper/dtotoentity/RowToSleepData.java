package com.seebie.server.mapper.dtotoentity;

import com.seebie.server.dto.SleepData;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.util.function.Function;

import static com.seebie.server.mapper.entitytodto.ZonedDateTimeToString.parse;
import static com.seebie.server.service.SleepService.HEADER;

@Component
public class RowToSleepData implements Function<CSVRecord, SleepData> {

    @Override
    public SleepData apply(CSVRecord record) {

        // TODO maybe use headers with enums
        //  {"Time-Asleep","Time-Awake","Duration-Minutes","Num-Times-Up","Notes"};

        var start = parse(record.get(HEADER[0]));
        var end = parse(record.get(HEADER[1]));
        var numTimesUp = Integer.parseInt(record.get(HEADER[3]));
        var notes = record.get(HEADER[4]);

        return new SleepData(notes, numTimesUp, start, end);
    }
}
