package com.seebie.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static java.time.Instant.now;
import static java.util.Optional.ofNullable;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.joining;
import static java.util.stream.StreamSupport.stream;

@Component
public class LoggingFilterRps implements Filter {

    private static Logger LOG = LoggerFactory.getLogger(LoggingFilterRps.class);

    private final String legend = "[reqs, avg-ms, max-ms]";
    private final Runnable logger = () -> log(getAndResetStatistics());
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(50);
    private final ConcurrentHashMap<Long, AtomicLong> timeToRequestCount = new ConcurrentHashMap<>();

    private final String timeFormat = "yyyy-MM-dd hh:mm:ss";
    private final String zone = "America/New_York";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat).withZone(ZoneId.of(zone));

    public LoggingFilterRps() {
        executor.scheduleAtFixedRate(logger, 0, 1, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdownNow();
    }

    public String toString(Cookie cookie) {
        return cookie.getName() + ": " + cookie.getValue();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {


//        HttpServletRequest request = (HttpServletRequest)req;
//
//        String headers = stream(spliteratorUnknownSize(request.getHeaderNames().asIterator(), ORDERED),false)
//                .map(h -> "Header - " + h + ": " + request.getHeader(h))
//                .collect(joining(System.lineSeparator()));
//
//        Cookie[] reqCookies = ofNullable(request.getCookies()).orElse(new Cookie[]{});
//        String cookies = Arrays.asList(reqCookies).stream()
//                .map(c -> "Cookie - " + toString(c))
//                .collect(joining(System.lineSeparator()));
//
//        System.out.println(request.getRequestURI());
//        System.out.println(headers);
//        System.out.println(cookies);
//        System.out.println();


        long startTime = System.currentTimeMillis();
        chain.doFilter(req, res);
        long elapsed = System.currentTimeMillis() - startTime;

//        System.out.println(request.getRequestURI());
//        System.out.println(headers);
//        System.out.println(cookies);
//        System.out.println();

        executor.submit(() -> accumulateStatistic(elapsed));
    }

    private void accumulateStatistic(Long elapsed) {
        timeToRequestCount.computeIfAbsent(elapsed, b -> new AtomicLong(0L));
        timeToRequestCount.get(elapsed).incrementAndGet();
    }

    private void log(List<RequestDurationCount> histogram) {

//        var logTime = formatter.format(now());

        var maxTimeMs = histogram.stream()
                .mapToLong(RequestDurationCount::requestDurationMs)
                .max()
                .orElse(0L);

        var totalRequests = histogram.stream()
                .mapToLong(RequestDurationCount::requestCount)
                .sum();

        var totalTime = histogram.stream()
                .mapToLong(RequestDurationCount::getTimeSpent)
                .sum();

        var avgResponseTime = Math.round((double)totalTime / (double)totalRequests);
        var requestLog = "[" + totalRequests + ", " + avgResponseTime + ", " + maxTimeMs + "]";

//        LOG.info(legend + ": " + requestLog);
    }

    // copy and clear values atomically without locking the map
    // then can work on the copy without synchronization
    private List<RequestDurationCount> getAndResetStatistics() {

        List<RequestDurationCount> durations = new ArrayList<>();

        timeToRequestCount.forEachEntry(1024, entry -> {
            long requestCountPerDuration = entry.getValue().getAndSet(0L);
            long requestDuration = entry.getKey();
            if(requestCountPerDuration != 0L) {
                durations.add(new RequestDurationCount(requestDuration, requestCountPerDuration));
            }
        });

        return durations;
    }

    record RequestDurationCount(long requestDurationMs, long requestCount) {
        public long getTimeSpent() {
            return requestDurationMs() * requestCount();
        }
    }
}
