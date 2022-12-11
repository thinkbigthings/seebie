package com.seebie.server.mapper.entitytospec;

import com.seebie.server.entity.StoreItem_;
import org.springframework.data.jpa.domain.Specification;
import com.seebie.server.entity.StoreItem;

import java.time.Instant;
import java.util.function.Function;

public class InstantToSpec implements Function<Instant, Specification<StoreItem>> {

    @Override
    public Specification<StoreItem> apply(Instant lastScanTime) {
        return (root, query, criteria) -> criteria.greaterThan(root.get(StoreItem_.ADDED), lastScanTime);
    }

}
