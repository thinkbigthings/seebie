package com.seebie.server.dto;

import java.time.ZonedDateTime;

public record DateRange(ZonedDateTime from, ZonedDateTime to) {

    public DateRange {
        if(to.isBefore(from)) {
            throw new IllegalArgumentException("\"from\" must be before \"to\"");
        }
    }
}
