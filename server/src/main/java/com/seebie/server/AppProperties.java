package com.seebie.server;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="app")
public record AppProperties(Integer apiVersion) { }