package com.seebie.server.mapper.entitytodto;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

public class ZonedDateTimeConverter {

    public static String format(ZonedDateTime dateTime) {
        return dateTime.format(ISO_OFFSET_DATE_TIME);
    }

    public static ZonedDateTime parse(String formatted) {
        return ZonedDateTime.parse(formatted, ISO_OFFSET_DATE_TIME);
    }
}
