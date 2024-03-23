package com.seebie.server;

import com.seebie.server.dto.FilterList;
import com.seebie.server.dto.HistogramRequest;
import com.seebie.server.service.FilterResult;
import com.seebie.server.service.HistogramCalculator;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.IntStream;

import static com.seebie.server.test.data.TestData.create30DayRange;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistogramCalculatorTest {

    private HistogramCalculator calculator = new HistogramCalculator();

    @Test
    public void testNoData() {

        var result = calculator.buildNormalizedHistogram(60, List.of());

        assertEquals(0, result.bins().size());
    }

    @Test
    public void testPartialData() {

        var dataSet1 = new FilterResult(create30DayRange(0), List.of(60, 120, 120));
        var dataSet2 = new FilterResult(create30DayRange(0), List.of());

        var result = calculator.buildNormalizedHistogram(60, List.of(dataSet1, dataSet2));

        assertEquals(2, result.bins().size());
        assertEquals(List.of(60, 120), result.bins());
        assertEquals(List.of(33, 67), result.dataSets().get(0));
        assertEquals(List.of(0,0), result.dataSets().get(1));
    }

    @Test
    public void testStackedNormalizedHistograms() {

        var req = new HistogramRequest(15, new FilterList(List.of()));

        final int binSize = req.binSize();

        List<Integer> durationMinutes1 = IntStream.range(4 * 60, 8 * 60).boxed().toList();
        List<Integer> durationMinutes2 = IntStream.range(5 * 60, 7 * 60).boxed().toList();

        var dataSet1 = new FilterResult(create30DayRange(0), durationMinutes1);
        var dataSet2 = new FilterResult(create30DayRange(0), durationMinutes2);


        var result = calculator.buildNormalizedHistogram(binSize, List.of(dataSet1, dataSet2));

        assertEquals(16, result.bins().size());
        assertEquals(2, result.dataSets().size());
        assertEquals(0, result.dataSets().get(1).get(0));
    }

}
