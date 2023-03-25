package com.seebie.server.mapper.entitytodto;

import com.seebie.server.dto.SleepData;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

@Component
public class SleepCsvMapper implements Function<SleepData, String[]> {

    @Override
    public String[] apply(SleepData data) {

        return new String[] {
                        data.startTime().truncatedTo(ChronoUnit.SECONDS).format(ISO_OFFSET_DATE_TIME),
                        data.stopTime().truncatedTo(ChronoUnit.SECONDS).format(ISO_OFFSET_DATE_TIME),
                        Long.toString(Duration.between(data.startTime(), data.stopTime()).toMinutes()),
                        data.notes()
                    };
    }
}
