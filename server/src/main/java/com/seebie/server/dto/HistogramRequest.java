package com.seebie.server.dto;

import com.seebie.server.validation.OrderedDates;

public record HistogramRequest(int binSize, @OrderedDates FilterList filters) {

}


