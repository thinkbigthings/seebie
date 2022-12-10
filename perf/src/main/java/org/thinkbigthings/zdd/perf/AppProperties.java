package org.thinkbigthings.zdd.perf;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix="connect")
public record AppProperties(String host, boolean insertOnly, Integer threads, Duration testDuration) { }