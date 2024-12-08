package com.seebie.server.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;

/**
 *
 * @param x date only
 * @param y units of hours
 */
public record SleepDataPoint(@NotNull LocalDate x, @PositiveOrZero BigDecimal y) {

    public SleepDataPoint(LocalDateTime stopTime, int minutes, String zoneId) {
        this(stopTime.toLocalDate(), valueOf((double) minutes / 60).setScale(2, HALF_UP));
    }

}
