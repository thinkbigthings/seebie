package com.seebie.server;

import com.seebie.server.dto.HistogramNormalized;
import com.seebie.server.dto.HistogramRequest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistogramTest {


    @Test
    public void updateUser() {

        var req = new HistogramRequest(15, List.of());

        final int binSize = req.binSizeMinutes();

        // TODO look into database group by with Postgres / JPQL

        List<Integer> set1 = IntStream.range(4 * 60, 8 * 60).boxed().toList();
        List<Integer> set2 = IntStream.range(5 * 60, 7 * 60).boxed().toList();

        var result = calculate(binSize, List.of(set1, set2));

        assertEquals(16, result.bins().size());
        assertEquals(2, result.dataSets().size());
        assertEquals(0, result.dataSets().get(1).get(0));

    }

    private List<Double> normalizeToBins(List<Integer> allBins, Map<Integer, Long> histogram) {

        // TODO normalized as an int percent

        var totalObs = histogram.values().stream().reduce(0L, (a, b) -> a + b);
        return allBins.stream()
                .map(b -> (double) histogram.getOrDefault(b, 0L) / (double) totalObs)
                .toList();
    }

    private Map<Integer, Long> buildHistogram(int binSize, List<Integer> values) {
        return values.stream()
                .map(i -> i / binSize)
                .collect(groupingBy(i -> i * binSize, counting()));
    }

    public HistogramNormalized calculate(final int binSize, List<List<Integer>> multiDataSets) {

        var multiHistograms = multiDataSets.stream()
                .map(data -> buildHistogram(binSize, data))
                .toList();

        // a bin is defined by its lower bound, and it is a closed interval at the bottom and open at the top

        // build a merged set of bins

        int minBin = multiHistograms.stream()
                .flatMap(s -> s.keySet().stream())
                .min(Integer::compareTo)
                .orElseThrow();

        int maxBin = multiHistograms.stream()
                .flatMap(s -> s.keySet().stream())
                .max(Integer::compareTo)
                .orElseThrow();

//        var bins = iterate(minBin, b -> b < maxBin, b-> b + binSize).boxed().toList();

        var bins = new ArrayList<Integer>();
        for (int b = minBin; b <= maxBin; b += binSize) {
            bins.add(b);
        }

        var stackedNormalizedData = multiHistograms.stream()
                .map(histData -> normalizeToBins(bins, histData))
                .toList();

        return new HistogramNormalized(bins, stackedNormalizedData);
    }

}
