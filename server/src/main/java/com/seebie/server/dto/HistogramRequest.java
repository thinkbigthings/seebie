package com.seebie.server.dto;

import com.seebie.server.validation.OrderedDates;

public record HistogramRequest(int binSizeMinutes, @OrderedDates FilterList filters) {

}


