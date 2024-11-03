package com.seebie.server.service;

import com.seebie.server.dto.StackedHistograms;

import java.util.*;

import static java.lang.Math.round;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;
import static java.util.stream.LongStream.iterate;

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
    public StackedHistograms buildNormalizedHistogram(final int binSize, final List<List<Long>> multiDataSets) {

        // TODO put the bin calculator somewhere else (use gatherer later?)
        // Where should it go? Move it and other histogram work into the originating service?
        // then watch the complexity

        // TODO data type should be short, not long. Long is still signed.
        // run both ways and watch memory usage for large data sets

        // TODO buildHistogram() should return the list of values that aligns with the list of bins
        // and put that and normalization as methods in the StackedHistograms class

        // build a complete set of bins that can account for all the data sets
        // if there was no data in any of the incoming data sets, the bin list will be the empty set
        LongSummaryStatistics stats = multiDataSets.stream()
                .flatMap(List::stream)
                .collect(summarizingLong(i->i));

        long lowestBin =  (stats.getMin() / binSize) * binSize; // get the lowest bin that is a multiple of binSize
        List<Long> unifiedBins = iterate(lowestBin, b -> b <= stats.getMax(), b -> b + binSize)
                .boxed()
                .toList();

        var stackedNormalizedHist = multiDataSets.stream()
                .map(durations -> buildHistogram(unifiedBins, durations))
                .map(this::normalizeValues)
                .toList();

        return new StackedHistograms(unifiedBins, stackedNormalizedHist);
    }

    /**
     * Build a histogram of the data.
     * The histogram is a map of bin lower bound to count of values in that bin.
     * A bin lower bound is the value divided by the bin size, rounded down, times the bin size.
     * A bin upper bound is the bin lower bound plus the bin size.
     * A bin is a closed interval at the bottom and open at the top.
     */
    private Map<Long, Long> buildHistogram(List<Long> binLowerBounds, List<Long> durationMinutes) {

        NavigableMap<Long, Long> histogram = new TreeMap<>();
        binLowerBounds.forEach(lower -> histogram.put(lower, 0L));

        var foundValues = durationMinutes.stream()
                .map(histogram::floorKey)
                .collect(groupingBy(identity(), counting()));

        histogram.putAll(foundValues);

        return histogram;
    }

    private List<Long> normalizeValues(Map<Long, Long> histogram) {

        var totalObservations = histogram.values().stream().mapToDouble(Double::valueOf).sum();

        return histogram.values().stream()
                .map(value -> round(100 * value / totalObservations))
                .toList();
    }

}
