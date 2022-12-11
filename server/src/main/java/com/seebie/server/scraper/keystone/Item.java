package com.seebie.server.scraper.keystone;

import com.seebie.server.entity.Subspecies;

import java.math.BigDecimal;

public record Item(Subspecies subspecies, String strain,
                   BigDecimal thc, BigDecimal cbd,
                   BigDecimal weightGrams, Long priceDollars, String vendor) {

}
