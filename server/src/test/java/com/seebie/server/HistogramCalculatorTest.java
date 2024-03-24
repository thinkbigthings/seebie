package com.seebie.server;

import com.seebie.server.dto.FilterList;
import com.seebie.server.dto.HistogramRequest;
import com.seebie.server.service.FilterResult;
import com.seebie.server.service.HistogramCalculator;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.LongStream;

import static com.seebie.server.test.data.TestData.create30DayRange;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistogramCalculatorTest {

    private HistogramCalculator calculator = new HistogramCalculator();

    private FilterResult emptyResult = new FilterResult(create30DayRange(1), List.of());

    @Test
    public void testNoFilterResults() {

        var result = calculator.buildNormalizedHistogram(60, List.of());

        assertEquals(0, result.bins().size());
        assertEquals(0, result.dataSets().size());
    }

    @Test
    public void testFilterResultEmpty() {

        var result = calculator.buildNormalizedHistogram(60, List.of(emptyResult));

        assertEquals(0, result.bins().size());
    }


    @Test
    public void testFilterResultsPartial() {

        var dataSet1 = new FilterResult(create30DayRange(0), List.of(60L, 120L, 120L));
        var dataSet2 = emptyResult;

        var result = calculator.buildNormalizedHistogram(60, List.of(dataSet1, dataSet2));

        assertEquals(2, result.bins().size());
        assertEquals(List.of(60L, 120L), result.bins());
        assertEquals(List.of(33L, 67L), result.dataSets().get(0));
        assertEquals(List.of(0L,0L), result.dataSets().get(1));
    }

    @Test
    public void testStackedNormalizedHistograms() {

        var req = new HistogramRequest(15, new FilterList(List.of()));

        final int binSize = req.binSize();

        List<Long> durationMinutes1 = LongStream.range(4 * 60, 8 * 60).boxed().toList();
        List<Long> durationMinutes2 = LongStream.range(5 * 60, 7 * 60).boxed().toList();

        var dataSet1 = new FilterResult(create30DayRange(0), durationMinutes1);
        var dataSet2 = new FilterResult(create30DayRange(0), durationMinutes2);

        var result = calculator.buildNormalizedHistogram(binSize, List.of(dataSet1, dataSet2));

        // check number of bins
        // check number of datasets should match number of filters
        assertEquals(16, result.bins().size());
        assertEquals(2, result.dataSets().size());

        // check number of histogram values should match number of bins
        assertEquals(result.bins().size(), result.dataSets().get(0).size());
        assertEquals(result.bins().size(), result.dataSets().get(1).size());

        // check actual histogram values for first dataset
        result.dataSets().get(0).forEach(histValue -> assertEquals(6, histValue));

        // check actual histogram values for second dataset: first 4 and last 4 are zero, middle set are 13
        result.dataSets().get(1).stream().limit(4).forEach(histValue -> assertEquals(0, histValue));
        result.dataSets().get(1).reversed().stream().limit(4).forEach(histValue -> assertEquals(0, histValue));
        result.dataSets().get(1).stream().skip(4).limit(8).forEach(histValue -> assertEquals(13, histValue));
    }

}
