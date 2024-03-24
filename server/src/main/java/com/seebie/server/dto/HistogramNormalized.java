package com.seebie.server.dto;

import java.util.List;

public record HistogramNormalized(List<Long> bins, List<List<Long>> dataSets) {

}
