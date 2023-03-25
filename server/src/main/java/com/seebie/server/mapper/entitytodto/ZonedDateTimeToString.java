package com.seebie.server.mapper.entitytodto;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

public class ZonedDateTimeToString implements Function<ZonedDateTime, String> {

    @Override
    public String apply(ZonedDateTime dateTime) {
        return dateTime.truncatedTo(ChronoUnit.MINUTES).format(ISO_OFFSET_DATE_TIME);
    }

    public static String format(ZonedDateTime dateTime) {
        return dateTime.truncatedTo(ChronoUnit.MINUTES).format(ISO_OFFSET_DATE_TIME);
    }
}
