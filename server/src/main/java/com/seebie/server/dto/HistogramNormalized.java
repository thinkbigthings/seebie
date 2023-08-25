package com.seebie.server.dto;

import java.util.List;

public record HistogramNormalized(List<Integer> bins, List<List<Integer>> dataSets) {

}
