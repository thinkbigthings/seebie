package com.seebie.server.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Immutable class representing a date range.
 * Use Validation annotations instead of the compact constructor to validate input
 * so that we have more control over whether validation happens or not, and we can build bad input for testing
 * the unhappy paths.
 *
 * @param from
 * @param to
 */
public record DateRange(LocalDate from, LocalDate to) {

    public static LocalDateTime atStartOfDay(LocalDate date) {
        return date.atTime(0, 0, 0);
    }

    public static LocalDateTime atEndOfDay(LocalDate date) {
        return date.atTime(23, 59, 59);
    }
}
