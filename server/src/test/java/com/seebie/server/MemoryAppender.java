package com.seebie.server;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * The appender is attached to the log system, so it is a global resource and subject to concurrency issues.
 * We make it thread safe by using a thread safe list.
 *
 * Using Collections.synchonizedList() helps but is still susceptible to concurrency issues
 * when iterating with a stream.
 */
public class MemoryAppender extends ListAppender<ILoggingEvent> {

    public MemoryAppender() {
        super();
        this.setName("MEMORY");
        this.list = new CopyOnWriteArrayList<>();
    }

    public List<ILoggingEvent> search(String... strings) {
        var searchList = Arrays.asList(strings);
        return this.list.stream()
                    .filter(event -> searchList.stream().filter(event.toString()::contains).count() == strings.length)
                    .collect(Collectors.toList());
    }

    public int getSize() {
        return this.list.size();
    }

    public List<ILoggingEvent> getLoggedEvents() {
        return Collections.unmodifiableList(this.list);
    }
}
