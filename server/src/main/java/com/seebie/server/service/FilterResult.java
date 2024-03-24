package com.seebie.server.service;

import com.seebie.server.dto.DateRange;

import java.util.List;

public record FilterResult(DateRange range, List<Long> durationMinutes) {}
