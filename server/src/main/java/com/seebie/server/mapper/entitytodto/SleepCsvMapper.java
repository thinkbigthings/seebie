package com.seebie.server.mapper.entitytodto;

import com.seebie.server.dto.SleepData;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Function;

import static com.seebie.server.mapper.entitytodto.ZonedDateTimeToString.format;

@Component
public class SleepCsvMapper implements Function<SleepData, String[]> {

    @Override
    public String[] apply(SleepData data) {

        return new String[] {
                        format(data.startTime()),
                        format(data.stopTime()),
                        Long.toString(Duration.between(data.startTime(), data.stopTime()).toMinutes()),
                        data.notes()
                    };
    }
}
