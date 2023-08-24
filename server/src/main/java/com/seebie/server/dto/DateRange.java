package com.seebie.server.dto;

import java.time.ZonedDateTime;

public record DateRange(ZonedDateTime from, ZonedDateTime to) {
}
