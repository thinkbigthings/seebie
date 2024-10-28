package com.seebie.server.dto;

import java.util.List;

public record StackedHistograms(List<Long> bins, List<List<Long>> histogramValues) {

}
