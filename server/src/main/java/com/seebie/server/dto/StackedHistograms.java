package com.seebie.server.dto;

import java.util.List;

// NOTE this needs to be synchronized with client side StackedHistograms
public record StackedHistograms(List<Long> bins, List<List<Long>> histogramValues) {

}
