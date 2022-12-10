package org.thinkbigthings.zdd.server.mapper.entitytospec;

import org.springframework.data.jpa.domain.Specification;
import org.thinkbigthings.zdd.server.entity.StoreItem;
import org.thinkbigthings.zdd.server.entity.StoreItem_;

import java.time.Instant;
import java.util.function.Function;

public class InstantToSpec implements Function<Instant, Specification<StoreItem>> {

    @Override
    public Specification<StoreItem> apply(Instant lastScanTime) {
        return (root, query, criteria) -> criteria.greaterThan(root.get(StoreItem_.ADDED), lastScanTime);
    }

}
