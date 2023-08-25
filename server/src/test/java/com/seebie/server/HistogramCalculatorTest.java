package com.seebie.server;

import com.seebie.server.dto.FilterList;
import com.seebie.server.dto.HistogramRequest;
import com.seebie.server.service.HistogramCalculator;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistogramCalculatorTest {

    private HistogramCalculator calculator = new HistogramCalculator();

    @Test
    public void testNoData() {

        var result = calculator.calculate(60, List.of());

        assertEquals(0, result.bins().size());
    }

    @Test
    public void testPartialData() {

        List<Integer> dataSet1 = List.of(60, 120, 120);
        List<Integer> dataSet2 = List.of();

        var result = calculator.calculate(60, List.of(dataSet1, dataSet2));

        assertEquals(2, result.bins().size());
        assertEquals(List.of(60, 120), result.bins());
        assertEquals(List.of(33, 67), result.dataSets().get(0));
        assertEquals(List.of(0,0), result.dataSets().get(1));
    }

    @Test
    public void testStackedNormalizedHistograms() {

        var req = new HistogramRequest(15, new FilterList(List.of()));

        final int binSize = req.binSizeMinutes();

        List<Integer> dataSet1 = IntStream.range(4 * 60, 8 * 60).boxed().toList();
        List<Integer> dataSet2 = IntStream.range(5 * 60, 7 * 60).boxed().toList();

        var result = calculator.calculate(binSize, List.of(dataSet1, dataSet2));

        assertEquals(16, result.bins().size());
        assertEquals(2, result.dataSets().size());
        assertEquals(0, result.dataSets().get(1).get(0));
    }

}
