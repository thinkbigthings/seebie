package com.seebie.server.mapper.entitytodto;

import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

/**
 * Keeping parsers and formatters in one place helps to ensure that the format is consistent.
 */
public class LocalDateTimeConverter {

    public static String format(LocalDateTime dateTime) {
        return dateTime.format(ISO_LOCAL_DATE_TIME);
    }

    public static LocalDateTime parse(String formatted) {
        return LocalDateTime.parse(formatted, ISO_LOCAL_DATE_TIME);
    }

}


