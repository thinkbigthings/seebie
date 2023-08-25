package com.seebie.server.dto;

import java.util.List;

public record HistogramRequest(int binSizeMinutes, List<DateRange> dataFilters) {


    public HistogramRequest {
        dataFilters.forEach(this::validateDateRange);
    }

    private void validateDateRange(DateRange dateRange) {
        if(dateRange.to().isBefore(dateRange.from())) {
            throw new IllegalArgumentException("Request parameter \"from\" must be before \"to\"");
        }
    }
}


