package org.thinkbigthings.zdd.server.scraper.keystone;

import org.thinkbigthings.zdd.server.entity.Subspecies;

import java.math.BigDecimal;
import java.util.List;

public record Item(Subspecies subspecies, String strain,
                   BigDecimal thc, BigDecimal cbd,
                   BigDecimal weightGrams, Long priceDollars, String vendor) {

}
