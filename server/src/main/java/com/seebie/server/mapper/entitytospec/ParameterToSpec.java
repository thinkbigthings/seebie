package com.seebie.server.mapper.entitytospec;

import com.seebie.server.entity.StoreItem_;
import org.springframework.data.jpa.domain.Specification;
import com.seebie.server.entity.SearchParameter;
import com.seebie.server.entity.StoreItem;
import com.seebie.server.entity.Subspecies;

import jakarta.persistence.criteria.Predicate;
import java.util.function.Function;

public class ParameterToSpec implements Function<SearchParameter, Specification<StoreItem>> {

    @Override
    public Specification<StoreItem> apply(SearchParameter search) {

        return (root, query, criteria) -> {

            // handle enum value searches
            Object equalsSearchValue = search.getValue();
            if(search.getField().equals(StoreItem_.SUBSPECIES)) {
                int ordinal = Integer.parseInt(search.getValue());
                equalsSearchValue = Subspecies.values()[ordinal];
            }

            // search field MUST match the specification field, e.g. StoreItem_.STRAIN
            Predicate persistencePredicate = switch(search.getOperator()) {
                case LT  -> criteria.lessThan(            root.get(search.getField()), search.getValue());
                case LTE -> criteria.lessThanOrEqualTo(   root.get(search.getField()), search.getValue());
                case EQ  -> criteria.equal(               root.get(search.getField()), equalsSearchValue);
                case GTE -> criteria.greaterThanOrEqualTo(root.get(search.getField()), search.getValue());
                case GT  -> criteria.greaterThan(         root.get(search.getField()), search.getValue());
            };

            return persistencePredicate;
        };
    }

}
