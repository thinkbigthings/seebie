package com.seebie.server.mapper.entitytospec;

import com.seebie.server.entity.StoreItem_;
import com.seebie.server.entity.Store_;
import org.springframework.data.jpa.domain.Specification;
import com.seebie.server.entity.StoreItem;

import java.util.List;
import java.util.function.Function;

public class StoreNamesToSpec implements Function<List<String>, Specification<StoreItem>> {

    // ways to produce always true:
    // criteriaBuilder.isTrue(criteriaBuilder.literal(true));
    // alternatively: criteriaBuilder.and()
    public static final Specification<StoreItem> TRUE = (root, query, criteria) -> criteria.isTrue(criteria.literal(true));

    /**
     * If stores list is empty, return Always True to search across all stores.
     *
     * @param storeNames
     * @return
     */
    @Override
    public Specification<StoreItem> apply(List<String> storeNames) {

        return storeNames.isEmpty()
                ? TRUE
                : (root, query, criteria) -> criteria.in(root.get(StoreItem_.STORE).get(Store_.NAME)).value(storeNames);
    }

}
