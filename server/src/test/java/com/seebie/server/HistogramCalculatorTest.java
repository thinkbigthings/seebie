package com.seebie.server;

import com.seebie.server.dto.FilterList;
import com.seebie.server.dto.HistogramRequest;
import com.seebie.server.service.HistogramCalculator;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistogramCalculatorTest {

    private HistogramCalculator calculator = new HistogramCalculator();

    private List<Long> emptyResult = List.of();

    @Test
    public void testNoFilterResults() {

        var result = calculator.buildNormalizedHistogram(60, List.of());

        assertEquals(0, result.bins().size());
        assertEquals(0, result.histogramValues().size());
    }

    @Test
    public void testFilterResultEmpty() {

        var result = calculator.buildNormalizedHistogram(60, List.of(emptyResult));

        assertEquals(0, result.bins().size());
    }


    @Test
    public void testFilterResultsPartial() {

        var dataSet1 = List.of(60L, 120L, 120L);
        var dataSet2 = emptyResult;

        var result = calculator.buildNormalizedHistogram(60, List.of(dataSet1, dataSet2));

        assertEquals(2, result.bins().size());
        assertEquals(List.of(60L, 120L), result.bins());
        assertEquals(List.of(33L, 67L), result.histogramValues().get(0));
        assertEquals(List.of(0L,0L), result.histogramValues().get(1));
    }

    @Test
    public void testStackedNormalizedHistograms() {

        var req = new HistogramRequest(15, new FilterList(List.of()));

        final int binSize = req.binSize();

        List<Long> durationMinutes1 = LongStream.range(4 * 60, 8 * 60).boxed().toList();
        List<Long> durationMinutes2 = LongStream.range(5 * 60, 7 * 60).boxed().toList();

        var result = calculator.buildNormalizedHistogram(binSize, List.of(durationMinutes1, durationMinutes2));

        // check number of bins
        // check number of datasets should match number of filters
        assertEquals(16, result.bins().size());
        assertEquals(2, result.histogramValues().size());

        // check number of histogram values should match number of bins
        assertEquals(result.bins().size(), result.histogramValues().get(0).size());
        assertEquals(result.bins().size(), result.histogramValues().get(1).size());

        // check actual histogram values for first dataset
        result.histogramValues().get(0).forEach(histValue -> assertEquals(6, histValue));

        // check actual histogram values for second dataset: first 4 and last 4 are zero, middle set are 13
        result.histogramValues().get(1).stream().limit(4).forEach(histValue -> assertEquals(0, histValue));
        result.histogramValues().get(1).reversed().stream().limit(4).forEach(histValue -> assertEquals(0, histValue));
        result.histogramValues().get(1).stream().skip(4).limit(8).forEach(histValue -> assertEquals(13, histValue));
    }

}
