package com.seebie.server.service;

import com.seebie.server.dto.HistogramNormalized;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.IntStream.iterate;

public class HistogramCalculator {

    /**
     * Return the histogram for comparison between multiple data sets of different sizes.
     * The normalized count is the count in a class divided by the total number of observations.
     * In this case the relative counts are normalized to sum to 100 since a percentage scale is used.
     *
     * @param binSize
     * @param multiDataSets
     * @return
     */
    public HistogramNormalized buildNormalizedHistogram(final int binSize, List<FilterResult> multiDataSets) {

        var multiHistograms = multiDataSets.stream()
                .map(result -> buildHistogram(binSize, result))
                .toList();

        // build a filled-in set of bins that can account for all the data sets
        var allBins = multiHistograms.stream().flatMap(s -> s.keySet().stream()).toList();
        var unifiedBins = buildUnifiedBins(binSize, allBins);

        var stackedNormalizedHist = multiHistograms.stream()
                .map(histData -> normalizeToBins(unifiedBins, histData))
                .toList();

        return new HistogramNormalized(unifiedBins, stackedNormalizedHist);
    }

    /**
     * Build a histogram of the data.
     * The histogram is a map of bin lower bound to count of values in that bin.
     * The bin size is specified by the binSize parameter.
     * A bin lower bound is the value divided by the bin size, rounded down, times the bin size.
     * A bin upper bound is the bin lower bound plus the bin size.
     * A bin is a closed interval at the bottom and open at the top.
     *
     * @param binSize
     * @param values
     * @return
     */
    private Map<Integer, Long> buildHistogram(int binSize, FilterResult values) {
        return values.durationMinutes().stream()
                .map(i -> i / binSize)
                .collect(groupingBy(i -> i * binSize, counting()));
    }

    /**
     * Build a set of bins that covers all the values.
     * The bin size is specified by the binSize parameter.
     * The bins are built from the minimum value to the maximum value, inclusive.
     *
     * @param binSize
     * @param allBins
     * @return
     */
    private List<Integer> buildUnifiedBins(final int binSize, Collection<Integer> allBins) {

        if(allBins.isEmpty()) {
            return List.of();
        }

        var minBin = Collections.min(allBins);
        var maxBin = Collections.max(allBins);

        return iterate(minBin, b -> b <= maxBin, b -> b + binSize).boxed().toList();
    }

    /**
     * Normalize the histogram values to a percentage of the total observations.
     * If there is a bin with no observations, it will be included in the result with a value of 0.
     *
     * @param allBins a complete set of bins which may be larger than the set of bins in the given histogram.
     * @param histogram the histogram to normalize
     *
     * @return a list of normalized values for each bin in the allBins parameter
     */
    private List<Integer> normalizeToBins(List<Integer> allBins, Map<Integer, Long> histogram) {

        var totalObservations = (double)histogram.values().stream().reduce(0L, (a, b) -> a + b);

        return allBins.stream()
                .map(b -> (double) histogram.getOrDefault(b, 0L) / totalObservations)
                .map(d -> Long.valueOf(Math.round(d * 100)).intValue())
                .toList();
    }

}
